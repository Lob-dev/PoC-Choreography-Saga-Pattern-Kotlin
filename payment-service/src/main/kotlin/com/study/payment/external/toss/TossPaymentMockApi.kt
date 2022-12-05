package com.study.payment.external.toss

import org.springframework.stereotype.Service
import java.util.*

@Service
class TossPaymentMockApi {

    fun purchase(request: TossPaymentRequest): String {
        // "Do Someting"
        val number = (1..2).random()
        val isOdd: Boolean = number % 2 == 0
        if (isOdd) {
            throw ExceedCreditLimitException()
        }
        return UUID.randomUUID().toString()
    }
}
