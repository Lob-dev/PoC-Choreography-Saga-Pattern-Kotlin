package com.study.stock.domain

import javax.persistence.*

@Entity
@Table(name = "stock")
class Stock(
    val productId: Long,
    var productCount: Int,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) {

    fun deduct(deductCount: Int) {
        if (productCount - deductCount < 0) {
            throw IllegalArgumentException()
        }
        this.productCount -= deductCount
    }

    fun sum(remainCount: Int) {
        productCount += remainCount
    }
}
