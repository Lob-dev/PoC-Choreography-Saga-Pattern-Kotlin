package com.study.order.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.study.common.message.CustomSqsMessageTemplate
import com.study.common.message.RouteMessageChannels
import com.study.common.message.RouteMessageChannels.*
import com.study.common.message.utils.SqsMessageChannelUtils
import com.study.order.config.properties.AwsSqsClientProperty
import io.awspring.cloud.messaging.core.QueueMessageChannel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MappingJackson2MessageConverter

@Configuration
class SqsConfiguration {

    @Bean
    protected fun customSqsMessageTemplate(
        amazonSQS: AmazonSQSAsync,
        objectMapper: ObjectMapper,
        @Value("\${message.wait-time-to-mills}") waitTimeToMills: Long,
    ): CustomSqsMessageTemplate {
        val orderCreateEventQueueUrl =
            amazonSQS.getQueueUrl(ORDER_CREATED_EVENT_QUEUE.queueName).queueUrl
        val paymentSuccessEventQueueUrl =
            amazonSQS.getQueueUrl(PAYMENT_SUCCESS_EVENT_QUEUE.queueName).queueUrl
        val stockDeductionFailedEventQueueUrl =
            amazonSQS.getQueueUrl(STOCK_DEDUCTION_FAILED_EVENT_QUEUE.queueName).queueUrl
        val paymentFailedEventQueueUrl =
            amazonSQS.getQueueUrl(PAYMENT_FAILED_EVENT_WITH_ORDER_QUEUE.queueName).queueUrl

        val messageChannels = mapOf(
            ORDER_CREATED_EVENT_QUEUE to QueueMessageChannel(amazonSQS, orderCreateEventQueueUrl),
            STOCK_DEDUCTION_FAILED_EVENT_QUEUE to QueueMessageChannel(
                amazonSQS,
                stockDeductionFailedEventQueueUrl
            ),
            PAYMENT_SUCCESS_EVENT_QUEUE to QueueMessageChannel(
                amazonSQS,
                paymentSuccessEventQueueUrl
            ),
            PAYMENT_FAILED_EVENT_WITH_ORDER_QUEUE to QueueMessageChannel(
                amazonSQS,
                paymentFailedEventQueueUrl
            ),
        )
        return CustomSqsMessageTemplate(objectMapper, messageChannels, waitTimeToMills)
    }

    @Bean
    protected fun mappingJackson2MessageConverter(objectMapper: ObjectMapper): MappingJackson2MessageConverter =
        MappingJackson2MessageConverter().apply {
            isStrictContentTypeMatch = false
            setObjectMapper(objectMapper)
        }

    @Bean
    fun amazonSQS(awsSqsClientProperty: AwsSqsClientProperty): AmazonSQSAsync =
        AmazonSQSAsyncClientBuilder.standard()
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(
                    awsSqsClientProperty.endPoint.uri,
                    awsSqsClientProperty.region.static,
                )
            )
            .withCredentials(
                AWSStaticCredentialsProvider(
                    BasicAWSCredentials(
                        awsSqsClientProperty.credentials.accessKey,
                        awsSqsClientProperty.credentials.secretKey,
                    )
                )
            )
            .build()
            .apply { SqsMessageChannelUtils.init(this, RouteMessageChannels.getChannels()) }
}
