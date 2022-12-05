package com.study.common.message

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.common.message.utils.MessageHeaderGenerator
import mu.KLogger
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.Message
import org.springframework.messaging.support.AbstractMessageChannel
import org.springframework.messaging.support.MessageBuilder
import java.util.*

class CustomSqsMessageTemplate(
    private val objectMapper: ObjectMapper,
    private val messageChannels: Map<RouteMessageChannels, AbstractMessageChannel>,
    @Value("\${message.wait-time-to-mills}")
    private var waitTimeToMills: Long,
) {
    private val logger: KLogger = KotlinLogging.logger { }

    fun <T> convertAndSend(
        routeKey: RouteMessageChannels,
        payload: T,
        messageId: String = UUID.randomUUID().toString(),
    ): Boolean {
        val targetMessageChannel: AbstractMessageChannel = messageChannels[routeKey]
            ?: throw RuntimeException("not found channel")

        val message = convertMessage(routeKey.queueGroupId, convertPayload(payload), messageId)
        logger.debug { "convertMessage = $message" }

        return try {
            targetMessageChannel.send(message, waitTimeToMills)
        } catch (exception: Exception) {
            logger.info { "CompositeSqsMessageTemplate.convertAndSend() throw ${exception.javaClass} ${exception.localizedMessage} ${message.javaClass}" }
            false
        }
    }

    private fun <T> convertPayload(payload: T): String = objectMapper.writeValueAsString(payload)

    private fun convertMessage(
        queueGroupId: String,
        payload: String,
        messageId: String,
    ): Message<String> =
        MessageBuilder.withPayload(payload)
            .copyHeaders(MessageHeaderGenerator.generate(queueGroupId, messageId))
            .build()
}
