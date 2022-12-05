package com.study.stock.external.order

import feign.Headers
import feign.Param
import feign.RequestLine
import org.springframework.cloud.openfeign.FeignClient

@FeignClient(name = "orderClient", url = "http://localhost:8080")
interface OrderApi {

    @RequestLine(value = "GET /api/orders/{orderId}/product/detail")
    @Headers("Content-Type: application/json")
    fun findProductDetail(@Param orderId: Long): OrderProductDetailResponse
}
