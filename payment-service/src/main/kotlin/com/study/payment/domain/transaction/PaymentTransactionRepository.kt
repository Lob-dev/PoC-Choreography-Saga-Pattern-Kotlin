package com.study.payment.domain.transaction

import org.springframework.data.jpa.repository.JpaRepository

interface PaymentTransactionRepository : JpaRepository<PaymentTransaction, Long> {
}
