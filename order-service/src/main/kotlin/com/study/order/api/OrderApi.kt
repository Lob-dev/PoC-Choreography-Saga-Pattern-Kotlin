package com.study.order.api

import com.study.order.application.OrderFacadeService
import com.study.order.domain.order.OrderService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/orders")
class OrderApi(
    private val orderFacadeService: OrderFacadeService,
    private val orderService: OrderService,
) {

    @PostMapping
    fun newOrder(@RequestBody @Valid request: NewOrderRequest) {
        orderFacadeService.newOrder(request)
    }

    /* Zero Payload 지원을 위한 조회 API */
    @GetMapping("/{orderId}/product/detail")
    fun findProductDetails(@PathVariable orderId: Long): OrderProductDetailResponse {
        val order = orderService.findBy(orderId)
        return OrderProductDetailResponse(order.productId, order.productCount)
    }

    @GetMapping("/{orderId}/payment/detail")
    fun findProductPrice(@PathVariable orderId: Long): OrderPaymentDetailResponse {
        val order = orderService.findBy(orderId)
        return OrderPaymentDetailResponse(order.productId, order.productPrice)
    }
}
