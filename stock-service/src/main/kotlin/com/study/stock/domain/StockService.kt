package com.study.stock.domain

import com.study.common.persistence.TransactionHandler
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StockService(
    private val stockRepository: StockRepository,
    transactionHandler: TransactionHandler,
) {

    init {
        transactionHandler.runInTransaction{
            stockRepository.save(Stock(1, 100))
        }
    }

    @Transactional(readOnly = true)
    fun findByProductId(productId: Long): Stock = stockRepository.findByProductId(productId)
        ?: throw RuntimeException("Not Found Stock")

    @Transactional(readOnly = true)
    fun findById(stockId: Long): Stock = stockRepository.findByIdOrNull(stockId)
        ?: throw RuntimeException("Not Found Stock")
}