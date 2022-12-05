package com.study.order.api

import com.study.order.domain.order.Order
import javax.validation.constraints.Min

data class NewOrderRequest(
    @get:Min(1)
    val productId: Long,
    @get:Min(0)
    val productPrice: Int,
    @get:Min(1)
    val productCount: Int
) {

    fun toOrder(): Order = Order.create(productId, productPrice, productCount)
}
