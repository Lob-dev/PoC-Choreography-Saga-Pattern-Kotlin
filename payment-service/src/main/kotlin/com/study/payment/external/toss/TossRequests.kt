package com.study.payment.external.toss

class TossPaymentRequest(
    val method: MethodType,
    val cardNumber: String,
    val cardExpirationYear: String,
    val cardExpirationMonth: String,
    val orderId: String,
    val amount: Int,
) {

    enum class MethodType { CARD }
}
