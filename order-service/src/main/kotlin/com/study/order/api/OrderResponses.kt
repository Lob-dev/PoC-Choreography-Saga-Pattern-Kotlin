package com.study.order.api

data class OrderProductDetailResponse(
    val productId: Long,
    val productCount: Int
)

data class OrderPaymentDetailResponse(
    val productId: Long,
    val productPrice: Int,
    val cardNumber: String = "000-0000-0000",
    val cardExpirationYear: String = "25",
    val cardExpirationMonth: String = "11",
    val method: String = "CARD"
)
