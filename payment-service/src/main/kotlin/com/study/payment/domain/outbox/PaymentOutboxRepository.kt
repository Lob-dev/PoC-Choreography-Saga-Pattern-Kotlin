package com.study.payment.domain.outbox

import com.study.common.message.OutboxStatus
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentOutboxRepository : JpaRepository<PaymentOutbox, Long> {
    fun findTopByOutboxStatus(outboxStatus: OutboxStatus): PaymentOutbox?
}
