package com.study.common.persistence

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.function.Supplier

@Component
class TransactionHandler {

    @Transactional(propagation = Propagation.REQUIRED)
    fun <T> runInTransaction(supplier: Supplier<T>): T {
        return supplier.get()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    fun <T> runInReadTransaction(supplier: Supplier<T>): T {
        return supplier.get()
    }
}
