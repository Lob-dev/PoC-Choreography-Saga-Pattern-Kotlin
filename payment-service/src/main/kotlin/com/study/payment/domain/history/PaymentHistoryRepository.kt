package com.study.payment.domain.history

import org.springframework.data.jpa.repository.JpaRepository

interface PaymentHistoryRepository : JpaRepository<PaymentHistory, Long> {

}
