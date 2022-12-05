package com.study.stock.domain.outbox

import com.study.common.message.OutboxStatus
import org.springframework.data.jpa.repository.JpaRepository

interface StockOutboxRepository : JpaRepository<StockOutbox, Long> {
    fun findTopByOutboxStatus(outboxStatus: OutboxStatus): StockOutbox?
}
