package com.study.stock.domain.transaction

import com.study.common.exception.ExistTransactionIdentityException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class StockTransactionService(
    private val stockTransactionRepository: StockTransactionRepository,
) {

    fun createHistory(transactionId: String, transactionAt: LocalDateTime): StockTransaction = try {
        stockTransactionRepository.save(StockTransaction(transactionId, transactionAt))
    } catch (exception: Exception) {
        if (exception is DataIntegrityViolationException) {
            throw ExistTransactionIdentityException()
        }
        throw exception
    }
}
