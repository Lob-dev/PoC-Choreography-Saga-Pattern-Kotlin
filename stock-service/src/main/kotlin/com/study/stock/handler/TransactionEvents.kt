package com.study.stock.handler

data class OrderCreatedEvent(
    val transactionId: String,
    val orderId: Long,
)

data class StockDeductionFailedEvent(
    val transactionId: String,
    val orderId: Long,
)

data class StockDeductionSuccessEvent(
    val transactionId: String,
    val orderId: Long,
)

data class PaymentFailedEvent(
    val transactionId: String,
    val orderId: Long,
)
