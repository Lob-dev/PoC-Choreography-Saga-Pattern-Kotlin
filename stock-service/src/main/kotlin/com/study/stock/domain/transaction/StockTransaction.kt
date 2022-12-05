package com.study.stock.domain.transaction

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "stock_transaction")
class StockTransaction(
    @Column(unique = true)
    val transactionId: String,
    val transactionAt: LocalDateTime,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) {
}
