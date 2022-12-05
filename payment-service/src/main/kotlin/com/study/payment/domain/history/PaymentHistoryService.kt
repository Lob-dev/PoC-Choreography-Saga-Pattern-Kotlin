package com.study.payment.domain.history

import org.springframework.stereotype.Service

@Service
class PaymentHistoryService(
    private val paymentHistoryRepository: PaymentHistoryRepository,
) {

    fun createHistory(newPaymentHistory: PaymentHistory): PaymentHistory =
        paymentHistoryRepository.save(newPaymentHistory)
}
