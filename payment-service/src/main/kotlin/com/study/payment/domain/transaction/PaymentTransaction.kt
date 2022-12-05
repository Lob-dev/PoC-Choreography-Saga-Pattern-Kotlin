package com.study.payment.domain.transaction

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "payment_transaction")
class PaymentTransaction(
    @Column(unique = true)
    val transactionId: String,
    val transactionAt: LocalDateTime,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) {
}
