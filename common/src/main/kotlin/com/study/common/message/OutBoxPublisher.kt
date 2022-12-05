package com.study.common.message

interface OutBoxPublisher<T> {
    fun publish(outbox: T)
    fun onRelay()
}
