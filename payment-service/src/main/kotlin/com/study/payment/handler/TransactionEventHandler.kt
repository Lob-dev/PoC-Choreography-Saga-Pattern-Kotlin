package com.study.payment.handler

import com.study.common.annotation.EventHandler
import com.study.common.exception.ExistTransactionIdentityException
import com.study.payment.domain.history.PaymentHistory
import com.study.payment.domain.history.PaymentHistoryService
import com.study.payment.domain.outbox.PaymentOutBoxPublisher
import com.study.payment.domain.outbox.PaymentOutbox
import com.study.payment.domain.outbox.PaymentOutbox.EventType
import com.study.payment.domain.transaction.PaymentTransactionService
import com.study.payment.external.toss.ExceedCreditLimitException
import com.study.payment.external.order.OrderApi
import com.study.payment.external.toss.TossPaymentMockApi
import io.awspring.cloud.messaging.listener.Acknowledgment
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy
import io.awspring.cloud.messaging.listener.annotation.SqsListener
import java.time.LocalDateTime
import java.time.ZoneId

@EventHandler
class TransactionEventHandler(
    private val orderApi: OrderApi,
    private val paymentHistoryService: PaymentHistoryService,
    private val tossPaymentMockApi: TossPaymentMockApi,
    private val paymentOutBoxPublisher: PaymentOutBoxPublisher,
    private val paymentTransactionService: PaymentTransactionService,
) {

    @SqsListener(
        value = [STOCK_DEDUCTION_SUCCESS_EVENT_QUEUE],
        deletionPolicy = SqsMessageDeletionPolicy.NEVER
    )
    fun onSubscribe(command: StockDeductionSuccessEvent, acknowledgment: Acknowledgment) {
        val transactionAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        val paymentDetail = orderApi.findPaymentDetail(command.orderId)
        val request = paymentDetail.toTossPaymentRequest()
        try {
            val transactionId = tossPaymentMockApi.purchase(request)
            val paymentHistory =
                PaymentHistory.create(request.orderId, transactionAt, transactionId, paymentDetail)
            paymentHistoryService.createHistory(paymentHistory)
            paymentTransactionService.createHistory(command.transactionId, transactionAt)
        } catch (exception: Exception) {
            if (exception is ExceedCreditLimitException) {
                val paymentOutbox = PaymentOutbox.create(command.orderId, EventType.FAILED)
                paymentOutBoxPublisher.publish(paymentOutbox)
                acknowledgment.acknowledge()
            }

            if (exception is ExistTransactionIdentityException) {
                acknowledgment.acknowledge()
            }
            throw exception
        }
        val paymentOutbox = PaymentOutbox.create(command.orderId, EventType.SUCCESS)
        paymentOutBoxPublisher.publish(paymentOutbox)
    }

    companion object {
        const val STOCK_DEDUCTION_SUCCESS_EVENT_QUEUE = "stock-deduction-success-event-queue.fifo"
    }
}
