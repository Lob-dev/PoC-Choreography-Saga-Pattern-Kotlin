package com.study.stock.handler

import com.study.common.exception.ExistTransactionIdentityException
import com.study.common.persistence.TransactionHandler
import com.study.stock.domain.StockService
import com.study.stock.domain.history.StockHistoryService
import com.study.stock.domain.outbox.StockOutbox
import com.study.stock.domain.outbox.StockOutbox.EventType
import com.study.stock.domain.outbox.StockOutboxPublisher
import com.study.stock.domain.transaction.StockTransactionService
import com.study.stock.external.order.OrderApi
import io.awspring.cloud.messaging.listener.Acknowledgment
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy
import io.awspring.cloud.messaging.listener.annotation.SqsListener
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class TransactionEventHandler(
    private val orderApi: OrderApi,
    private val stockService: StockService,
    private val stockHistoryService: StockHistoryService,
    private val stockTransactionService: StockTransactionService,
    private val stockOutboxPublisher: StockOutboxPublisher,
    private val transactionHandler: TransactionHandler,
) {

    @SqsListener(value = [ORDER_CREATE_EVENT_QUEUE], deletionPolicy = SqsMessageDeletionPolicy.NEVER)
    fun onSubscribe(command: OrderCreatedEvent, acknowledgment: Acknowledgment) {
        val transactionAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        val orderId = command.orderId
        val productDetail = orderApi.findProductDetail(orderId)

        transactionHandler.runInTransaction {
            val deductCount = productDetail.productCount
            val stock = stockService.findByProductId(productDetail.productId)
            try {
                stock.deduct(deductCount)
                stockHistoryService.createHistory(stock.id!!, orderId, deductCount)
                stockTransactionService.createHistory(command.transactionId, transactionAt)

                val stockOutbox = StockOutbox.create(orderId, EventType.SUCCESS)
                stockOutboxPublisher.publish(stockOutbox)
            } catch (exception: Exception) {
                if (exception is ExistTransactionIdentityException) {
                    acknowledgment.acknowledge()
                    throw exception
                }

                if (exception is IllegalArgumentException) {
                    val stockOutbox =
                        StockOutbox.create(orderId, eventType = EventType.FAILED)
                    stockOutboxPublisher.publish(stockOutbox)
                    acknowledgment.acknowledge()
                }
                throw exception
            }
        }
    }

    @SqsListener(
        value = [PAYMENT_FAILED_EVENT_WITH_STOCK_QUEUE],
        deletionPolicy = SqsMessageDeletionPolicy.NEVER
    )
    fun onSubscribe(command: PaymentFailedEvent, acknowledgment: Acknowledgment) {
        val orderId = command.orderId
        transactionHandler.runInTransaction {
            val stockHistory = stockHistoryService.findByOrderId(orderId)
            val targetStock = stockService.findById(stockHistory.stockId)
            targetStock.sum(stockHistory.deductCount)
        }
    }

    companion object {
        const val ORDER_CREATE_EVENT_QUEUE = "order-create-event-queue.fifo"
        const val PAYMENT_FAILED_EVENT_WITH_STOCK_QUEUE = "payment-failed-event-with-stock-queue.fifo"
    }
}
