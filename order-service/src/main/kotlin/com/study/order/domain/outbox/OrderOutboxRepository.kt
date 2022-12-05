package com.study.order.domain.outbox

import com.study.common.message.OutboxStatus
import org.springframework.data.jpa.repository.JpaRepository

interface OrderOutboxRepository : JpaRepository<OrderOutbox, Long> {
    fun findTopByOutboxStatus(outboxStatus: OutboxStatus): OrderOutbox?
}
