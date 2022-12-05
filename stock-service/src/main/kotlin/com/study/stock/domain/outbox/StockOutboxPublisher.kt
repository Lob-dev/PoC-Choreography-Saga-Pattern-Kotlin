package com.study.stock.domain.outbox

import com.study.common.message.CustomSqsMessageTemplate
import com.study.common.message.OutBoxPublisher
import com.study.common.message.OutboxStatus
import com.study.common.message.RouteMessageChannels.STOCK_DEDUCTION_SUCCESS_EVENT_QUEUE
import com.study.common.message.RouteMessageChannels.STOCK_DEDUCTION_FAILED_EVENT_QUEUE
import com.study.stock.handler.StockDeductionFailedEvent
import com.study.stock.handler.StockDeductionSuccessEvent
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

@Service
class StockOutboxPublisher(
    private val stockOutboxRepository: StockOutboxRepository,
    private val customSqsMessageTemplate: CustomSqsMessageTemplate,
) : OutBoxPublisher<StockOutbox> {

    override fun publish(outbox: StockOutbox) {
        stockOutboxRepository.save(outbox)
    }

    @Scheduled(fixedDelay = 300, timeUnit = TimeUnit.MILLISECONDS, zone = "Asia/Seoul")
    @Transactional
    override fun onRelay() {
        val stockOutbox = stockOutboxRepository.findTopByOutboxStatus(OutboxStatus.CREATED)
            ?: return

        val publishAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        val isPublished = when (stockOutbox.eventType) {
            StockOutbox.EventType.SUCCESS -> {
                val event =
                    StockDeductionSuccessEvent(stockOutbox.transactionId, stockOutbox.orderId)
                customSqsMessageTemplate.convertAndSend(STOCK_DEDUCTION_SUCCESS_EVENT_QUEUE, event)
            }
            StockOutbox.EventType.FAILED -> {
                val event =
                    StockDeductionFailedEvent(stockOutbox.transactionId, stockOutbox.orderId)
                customSqsMessageTemplate.convertAndSend(STOCK_DEDUCTION_FAILED_EVENT_QUEUE, event)
            }
        }

        if (isPublished) {
            stockOutbox.complete(publishAt)
        }
    }
}
