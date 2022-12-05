package com.study.order.domain.transaction

import org.springframework.data.jpa.repository.JpaRepository

interface OrderTransactionRepository : JpaRepository<OrderTransaction, Long> {

}
