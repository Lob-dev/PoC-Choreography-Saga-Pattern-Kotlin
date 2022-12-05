package com.study.stock.domain.history

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StockHistoryService(
    private val stockHistoryRepository: StockHistoryRepository,
) {

    @Transactional
    fun createHistory(stockId: Long, orderId: Long, deductCount: Int): StockHistory =
        stockHistoryRepository.save(StockHistory(stockId, orderId, deductCount))

    @Transactional(readOnly = true)
    fun findByOrderId(orderId: Long): StockHistory =
        stockHistoryRepository.findByOrderId(orderId)
            ?: throw RuntimeException("Not Found Stock History")
}