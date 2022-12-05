package com.study.order.domain.order

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class OrderService(
    private val orderRepository: OrderRepository,
) {

    @Transactional
    fun createOrder(newOrder: Order): Order = orderRepository.save(newOrder)

    @Transactional(readOnly = true)
    fun findBy(orderId: Long): Order = orderRepository.findByIdOrNull(orderId)
        ?: throw RuntimeException("Not found order")

    @Transactional
    fun completeOrder(orderId: Long) {
        val targetOrder = findBy(orderId)
        targetOrder.complete()
    }

    @Transactional
    fun cancelOrder(orderId: Long) {
        val targetOrder = findBy(orderId)
        targetOrder.cancel()
    }
}
