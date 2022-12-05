package com.study.stock.domain.history

import org.springframework.data.jpa.repository.JpaRepository

interface StockHistoryRepository : JpaRepository<StockHistory, Long> {

    fun findByOrderId(orderId: Long): StockHistory?
}