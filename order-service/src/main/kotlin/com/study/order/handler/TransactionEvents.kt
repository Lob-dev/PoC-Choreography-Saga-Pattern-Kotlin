package com.study.order.handler

data class OrderCreatedEvent(
    val transactionId: String,
    val orderId: Long,
)

data class PaymentSuccessEvent(
    val transactionId: String,
    val orderId: Long,
)

data class PaymentFailedEvent(
    val transactionId: String,
    val orderId: Long,
)
