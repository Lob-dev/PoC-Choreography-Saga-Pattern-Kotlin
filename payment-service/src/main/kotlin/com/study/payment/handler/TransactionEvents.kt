package com.study.payment.handler

data class StockDeductionSuccessEvent(
    val transactionId: String,
    val orderId: Long,
)

data class PaymentFailedEvent(
    val transactionId: String,
    val stockId: Long,
)

data class PaymentSuccessEvent(
    val transactionId: String,
    val orderId: Long,
)
