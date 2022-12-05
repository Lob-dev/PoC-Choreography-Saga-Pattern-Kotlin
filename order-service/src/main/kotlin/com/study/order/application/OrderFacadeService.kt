package com.study.order.application

import com.study.common.persistence.TransactionHandler
import com.study.order.api.NewOrderRequest
import com.study.order.domain.order.OrderService
import com.study.order.domain.outbox.OrderOutBoxPublisher
import com.study.order.domain.outbox.OrderOutbox
import org.springframework.stereotype.Service

@Service
class OrderFacadeService(
    private val orderService: OrderService,
    private val orderOutBoxPublisher: OrderOutBoxPublisher,
    private val transactionHandler: TransactionHandler,
) {

    fun newOrder(request: NewOrderRequest) {
        transactionHandler.runInTransaction {
            val newOrder = orderService.createOrder(request.toOrder())
            orderOutBoxPublisher.publish(OrderOutbox.create(newOrder.id!!))
        }
    }
}
