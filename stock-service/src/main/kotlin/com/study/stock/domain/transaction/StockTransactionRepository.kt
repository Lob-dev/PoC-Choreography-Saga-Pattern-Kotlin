package com.study.stock.domain.transaction

import org.springframework.data.jpa.repository.JpaRepository

interface StockTransactionRepository : JpaRepository<StockTransaction, Long> {
}