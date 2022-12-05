package com.study.order.domain.transaction

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "order_transaction")
class OrderTransaction(
    @Column(unique = true)
    val transactionId: String,
    val transactionAt: LocalDateTime,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) {
}

