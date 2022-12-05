package com.study.payment.config

import com.study.common.persistence.TransactionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SupportBeanConfiguration {

    @Bean
    protected fun transactionHandler(): TransactionHandler = TransactionHandler()
}
