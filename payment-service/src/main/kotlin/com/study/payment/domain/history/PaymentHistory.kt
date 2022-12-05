package com.study.payment.domain.history

import com.study.payment.external.order.OrderPaymentDetailResponse
import com.study.payment.external.toss.TossPaymentRequest
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table
class PaymentHistory(
    val transactionId: String,
    val transactionAt: LocalDateTime,
    val tossTransactionId: String,
    val productId: Long,
    val productPrice: Int,
    @Enumerated(EnumType.STRING)
    val method: TossPaymentRequest.MethodType,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    companion object {
        fun create(
            orderId: String,
            transactionAt: LocalDateTime,
            tossTransactionId: String,
            paymentDetail: OrderPaymentDetailResponse
        ): PaymentHistory = PaymentHistory(
            orderId,
            transactionAt,
            tossTransactionId,
            paymentDetail.productId,
            paymentDetail.productPrice,
            paymentDetail.method
        )
    }
}
