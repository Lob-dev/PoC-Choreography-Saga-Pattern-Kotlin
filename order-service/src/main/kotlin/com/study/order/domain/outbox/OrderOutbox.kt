package com.study.order.domain.outbox

import com.study.common.message.OutboxStatus
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "order_outbox")
class OrderOutbox(
    val transactionId: String,
    var publishAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    var outboxStatus: OutboxStatus = OutboxStatus.CREATED,
    val orderId: Long,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) {

    fun complete(publishAt: LocalDateTime) {
        this.publishAt = publishAt
        this.outboxStatus = OutboxStatus.PUBLISHED
    }

    companion object {
        fun create(orderId: Long): OrderOutbox =
            OrderOutbox(transactionId = UUID.randomUUID().toString(), orderId = orderId)
    }
}
