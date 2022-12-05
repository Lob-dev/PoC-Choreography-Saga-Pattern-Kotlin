package com.study.payment.external.order

import com.study.payment.external.toss.TossPaymentRequest
import java.util.*

data class OrderPaymentDetailResponse(
    val productId: Long,
    val productPrice: Int,
    val cardNumber: String,
    val cardExpirationYear: String,
    val cardExpirationMonth: String,
    val method: TossPaymentRequest.MethodType
) {

    fun toTossPaymentRequest(): TossPaymentRequest = TossPaymentRequest(
        method = TossPaymentRequest.MethodType.CARD,
        cardNumber = cardNumber,
        cardExpirationYear = cardExpirationYear,
        cardExpirationMonth = cardExpirationMonth,
        orderId = UUID.randomUUID().toString(),
        amount = productPrice,
    )
}
