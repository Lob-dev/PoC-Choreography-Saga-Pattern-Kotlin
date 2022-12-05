package com.study.order.handler

import com.study.common.annotation.EventHandler
import com.study.common.exception.ExistTransactionIdentityException
import com.study.common.persistence.TransactionHandler
import com.study.order.domain.order.OrderService
import com.study.order.domain.transaction.OrderTransactionService
import io.awspring.cloud.messaging.listener.Acknowledgment
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy
import io.awspring.cloud.messaging.listener.annotation.SqsListener
import java.time.LocalDateTime
import java.time.ZoneId

@EventHandler
class TransactionEventHandler(
    private val orderService: OrderService,
    private val orderTransactionService: OrderTransactionService,
    private val transactionHandler: TransactionHandler,
) {

    @SqsListener(
        value = [PAYMENT_SUCCESS_EVENT_QUEUE],
        deletionPolicy = SqsMessageDeletionPolicy.NEVER
    )
    fun onSubscribe(command: PaymentSuccessEvent, acknowledgment: Acknowledgment) {
        val transactionAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"))

        transactionHandler.runInTransaction {
            orderService.completeOrder(command.orderId)
            try {
                orderTransactionService.createHistory(command.transactionId, transactionAt)
            } catch (exception: Exception) {
                if (exception is ExistTransactionIdentityException) {
                    acknowledgment.acknowledge()
                    throw exception
                }
                throw exception
            }
        }
    }

    @SqsListener(
        value = [STOCK_DEDUCTION_FAILED_EVENT_QUEUE, PAYMENT_FAILED_EVENT_QUEUE],
        deletionPolicy = SqsMessageDeletionPolicy.NEVER
    )
    fun onSubscribe(command: PaymentFailedEvent, acknowledgment: Acknowledgment) {
        val transactionAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"))

        transactionHandler.runInTransaction {
            orderService.cancelOrder(command.orderId)
            try {
                orderTransactionService.createHistory(command.transactionId, transactionAt)
            } catch (exception: Exception) {
                if (exception is ExistTransactionIdentityException) {
                    acknowledgment.acknowledge()
                    throw exception
                }
                throw exception
            }
        }
    }

    companion object {
        const val PAYMENT_SUCCESS_EVENT_QUEUE = "payment-success-event-queue.fifo"
        const val STOCK_DEDUCTION_FAILED_EVENT_QUEUE = "stock-deduction-failed-event-queue.fifo"
        const val PAYMENT_FAILED_EVENT_QUEUE = "payment-failed-event-with-order-queue.fifo"
    }
}
