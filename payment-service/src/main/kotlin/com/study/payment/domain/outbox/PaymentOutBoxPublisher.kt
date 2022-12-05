package com.study.payment.domain.outbox

import com.study.common.message.CustomSqsMessageTemplate
import com.study.common.message.OutBoxPublisher
import com.study.common.message.OutboxStatus
import com.study.common.message.RouteMessageChannels.PAYMENT_FAILED_EVENT_SNS_TOPIC
import com.study.common.message.RouteMessageChannels.PAYMENT_SUCCESS_EVENT_QUEUE
import com.study.payment.handler.PaymentSuccessEvent
import com.study.payment.handler.PaymentFailedEvent
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

@Service
class PaymentOutBoxPublisher(
    private val paymentOutboxRepository: PaymentOutboxRepository,
    private val customSqsMessageTemplate: CustomSqsMessageTemplate,
) : OutBoxPublisher<PaymentOutbox> {

    override fun publish(outbox: PaymentOutbox) {
        paymentOutboxRepository.save(outbox)
    }

    @Scheduled(fixedDelay = 300, timeUnit = TimeUnit.MILLISECONDS, zone = "Asia/Seoul")
    @Transactional
    override fun onRelay() {
        val paymentOutbox = paymentOutboxRepository.findTopByOutboxStatus(OutboxStatus.CREATED)
            ?: return

        val publishAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        val isPublished = when (paymentOutbox.eventType) {
            PaymentOutbox.EventType.SUCCESS -> {
                val event = PaymentSuccessEvent(paymentOutbox.transactionId, paymentOutbox.orderId)
                customSqsMessageTemplate.convertAndSend(PAYMENT_SUCCESS_EVENT_QUEUE, event)
            }
            PaymentOutbox.EventType.FAILED -> {
                val event = PaymentFailedEvent(paymentOutbox.transactionId, paymentOutbox.orderId)
                customSqsMessageTemplate.convertAndSend(PAYMENT_FAILED_EVENT_SNS_TOPIC, event)
            }
        }

        if (isPublished) {
            paymentOutbox.complete(publishAt)
        }
    }
}
