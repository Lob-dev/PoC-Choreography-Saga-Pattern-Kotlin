package com.study.stock.domain

import org.springframework.data.jpa.repository.JpaRepository

interface StockRepository : JpaRepository<Stock, Long> {
    fun findByProductId(productId: Long): Stock?
}