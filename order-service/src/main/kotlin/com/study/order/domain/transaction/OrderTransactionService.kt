package com.study.order.domain.transaction

import com.study.common.exception.ExistTransactionIdentityException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class OrderTransactionService(
    private val orderTransactionRepository: OrderTransactionRepository,
) {

    fun createHistory(transactionId: String, transactionAt: LocalDateTime): OrderTransaction = try {
        orderTransactionRepository.save(OrderTransaction(transactionId, transactionAt))
    } catch (exception: Exception) {
        if (exception is DataIntegrityViolationException) {
            throw ExistTransactionIdentityException()
        }
        throw exception
    }
}
