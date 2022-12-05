package com.study.stock.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.study.common.message.CustomSqsMessageTemplate
import com.study.common.message.RouteMessageChannels.*
import com.study.stock.config.properties.AwsSqsClientProperty
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
        val orderEventUrl =
            amazonSQS.getQueueUrl(STOCK_DEDUCTION_FAILED_EVENT_QUEUE.queueName).queueUrl
        val stockEventUrl =
            amazonSQS.getQueueUrl(ORDER_CREATED_EVENT_QUEUE.queueName).queueUrl
        val paymentPurchaseEventUrl =
            amazonSQS.getQueueUrl(STOCK_DEDUCTION_SUCCESS_EVENT_QUEUE.queueName).queueUrl
        val paymentFailedEventUrl =
            amazonSQS.getQueueUrl(PAYMENT_FAILED_EVENT_WITH_STOCK_QUEUE.queueName).queueUrl

        val messageChannels = mapOf(
            STOCK_DEDUCTION_FAILED_EVENT_QUEUE to QueueMessageChannel(amazonSQS, orderEventUrl),
            ORDER_CREATED_EVENT_QUEUE to QueueMessageChannel(amazonSQS, stockEventUrl),
            STOCK_DEDUCTION_SUCCESS_EVENT_QUEUE to QueueMessageChannel(
                amazonSQS,
                paymentPurchaseEventUrl
            ),
            PAYMENT_FAILED_EVENT_WITH_STOCK_QUEUE to QueueMessageChannel(
                amazonSQS,
                paymentFailedEventUrl
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
}
