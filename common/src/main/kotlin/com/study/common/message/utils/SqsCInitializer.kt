package com.study.common.message.utils

import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.model.CreateQueueRequest
import com.study.common.message.RouteMessageChannels
import mu.KLogger
import mu.KotlinLogging

object SqsMessageChannelUtils {
    private val logger: KLogger = KotlinLogging.logger { }
    private val FIFO_ATTRIBUTES = mapOf("FifoQueue" to "true", "ContentBasedDeduplication" to "true")

    fun init(amazonSQSAsync: AmazonSQSAsync, queueChannels: List<RouteMessageChannels>) {
        try {
            queueChannels.forEach {
                amazonSQSAsync.createQueue(
                    CreateQueueRequest(it.queueName).withAttributes(FIFO_ATTRIBUTES)
                )
            }
        } catch (exception: Exception) {
            logger.info { "exception = ${exception.printStackTrace()}" }
        }
    }
}
