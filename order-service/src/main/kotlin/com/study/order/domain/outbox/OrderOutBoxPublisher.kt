package com.study.order.domain.outbox

import com.study.common.message.CustomSqsMessageTemplate
import com.study.common.message.OutBoxPublisher
import com.study.common.message.OutboxStatus
import com.study.common.message.RouteMessageChannels.ORDER_CREATED_EVENT_QUEUE
import com.study.order.handler.OrderCreatedEvent
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

@Service
class OrderOutBoxPublisher(
    private val orderOutboxRepository: OrderOutboxRepository,
    private val customSqsMessageTemplate: CustomSqsMessageTemplate,
) : OutBoxPublisher<OrderOutbox> {

    override fun publish(outbox: OrderOutbox) {
        orderOutboxRepository.save(outbox)
    }

    @Scheduled(fixedDelay = 300, timeUnit = TimeUnit.MILLISECONDS, zone = "Asia/Seoul")
    @Transactional
    override fun onRelay() {
        val orderOutbox = orderOutboxRepository.findTopByOutboxStatus(OutboxStatus.CREATED)
            ?: return

        val publishAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        val event = OrderCreatedEvent(orderOutbox.transactionId, orderOutbox.orderId)

        val isPublished = customSqsMessageTemplate.convertAndSend(
            ORDER_CREATED_EVENT_QUEUE,
            event,
            orderOutbox.transactionId,
        )
        if (isPublished) {
            orderOutbox.complete(publishAt)
        }
    }
}
