package com.study.payment.domain.transaction

import com.study.common.exception.ExistTransactionIdentityException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PaymentTransactionService(
    private val paymentTransactionRepository: PaymentTransactionRepository,
) {

    fun createHistory(transactionId: String, transactionAt: LocalDateTime): PaymentTransaction =
        try {
            paymentTransactionRepository.save(PaymentTransaction(transactionId, transactionAt))
        } catch (exception: Exception) {
            if (exception is DataIntegrityViolationException) {
                throw ExistTransactionIdentityException()
            }
            throw exception
        }
}
