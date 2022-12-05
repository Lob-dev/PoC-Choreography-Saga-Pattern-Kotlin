package com.study.stock.domain.history

import javax.persistence.*

@Entity
@Table(name = "stock_history")
class StockHistory(
    val orderId: Long,
    val stockId: Long,
    val deductCount: Int,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) {
}
