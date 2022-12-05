package com.study.order.domain.order

import javax.persistence.*

@Entity
@Table(name = "orders")
class Order(
    val productId: Long,
    val productPrice: Int,
    val productCount: Int,
    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) {

    enum class OrderStatus { CREATE, COMPLETED, CANCEL }

    // 결제 처리가 되었을 때
    fun complete() {
        orderStatus = OrderStatus.COMPLETED
    }

    // 재고 처리나, 결제가 실패하였을 때
    fun cancel() {
        orderStatus = OrderStatus.CANCEL
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other is Order) {
            return id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    companion object {
        fun create(productId: Long, productPrice: Int, productCount: Int): Order = Order(
            productId = productId,
            productPrice = productPrice,
            productCount = productCount,
            orderStatus = OrderStatus.CREATE,
        )
    }
}
