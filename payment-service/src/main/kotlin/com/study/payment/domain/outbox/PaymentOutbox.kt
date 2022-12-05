package com.study.payment.domain.outbox

import com.study.common.message.OutboxStatus
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "payment_outbox")
class PaymentOutbox(
    val transactionId: String,
    var publishAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    var outboxStatus: OutboxStatus = OutboxStatus.CREATED,
    val orderId: Long,
    val eventType: EventType,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) {

    enum class EventType { SUCCESS, FAILED }

    fun complete(publishAt: LocalDateTime) {
        this.publishAt = publishAt
        this.outboxStatus = OutboxStatus.PUBLISHED
    }

    companion object {
        fun create(orderId: Long, eventType: EventType): PaymentOutbox =
            PaymentOutbox(
                transactionId = UUID.randomUUID().toString(),
                orderId = orderId,
                eventType = eventType
            )
    }
}
