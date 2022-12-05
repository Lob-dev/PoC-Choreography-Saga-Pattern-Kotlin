package com.study.common.message

enum class RouteMessageChannels(
    val queueName: String,
    val queueGroupId: String,
) {
    // 주문
    ORDER_CREATED_EVENT_QUEUE("order-create-event-queue.fifo", "ORDER-CREATED-EVENT-QUEUE-GROUP"),
    PAYMENT_FAILED_EVENT_WITH_ORDER_QUEUE("payment-failed-event-with-order-queue.fifo", "PAYMENT-FAILED-EVENT-QUEUE-WITH-ORDER-GROUP"),

    // 재고
    STOCK_DEDUCTION_SUCCESS_EVENT_QUEUE("stock-deduction-success-event-queue.fifo", "STOCK-DEDUCTION-SUCCESS-EVENT-QUEUE-GROUP"),
    STOCK_DEDUCTION_FAILED_EVENT_QUEUE("stock-deduction-failed-event-queue.fifo", "STOCK-DEDUCTION-FAILED-EVENT-QUEUE-GROUP"),

    // 결제
    PAYMENT_FAILED_EVENT_SNS_TOPIC("payment-failed-sns-topic.fifo", "PAYMENT-FAILED-EVENT-SNS-TOPIC"),
    PAYMENT_SUCCESS_EVENT_QUEUE("payment-success-event-queue.fifo", "PAYMENT-SUCCESS-EVENT-QUEUE-GROUP"),
    PAYMENT_FAILED_EVENT_WITH_STOCK_QUEUE("payment-failed-event-with-stock-queue.fifo", "PAYMENT-FAILED-EVENT-QUEUE-WITH-STOCK-GROUP");

    companion object {
        fun getChannels(): List<RouteMessageChannels> = RouteMessageChannels.values()
            .filter { it != PAYMENT_FAILED_EVENT_SNS_TOPIC }
            .toList()
    }
}
