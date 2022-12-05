package com.study.payment.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSAsync
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder
import com.amazonaws.services.sns.model.CreateTopicRequest
import com.amazonaws.services.sns.util.Topics
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.study.common.message.CustomSqsMessageTemplate
import com.study.common.message.RouteMessageChannels.*
import com.study.payment.config.properties.AwsClientProperty
import io.awspring.cloud.messaging.core.QueueMessageChannel
import io.awspring.cloud.messaging.core.TopicMessageChannel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MappingJackson2MessageConverter


@Configuration
class SqsConfiguration {

    @Bean
    protected fun customMessageTemplate(
        amazonSNS: AmazonSNS,
        amazonSQS: AmazonSQSAsync,
        objectMapper: ObjectMapper,
        @Value("\${message.wait-time-to-mills}") waitTimeToMills: Long,
    ): CustomSqsMessageTemplate {
        val stockDeductionSuccessEventQueueUrl =
            amazonSQS.getQueueUrl(STOCK_DEDUCTION_SUCCESS_EVENT_QUEUE.queueName).queueUrl
        val paymentSuccessEventQueueUrl =
            amazonSQS.getQueueUrl(PAYMENT_SUCCESS_EVENT_QUEUE.queueName).queueUrl

        val topicAttributes: MutableMap<String, String> = HashMap()
        topicAttributes["FifoTopic"] = "true"
        topicAttributes["ContentBasedDeduplication"] = "false"

        val topicRequest: CreateTopicRequest = CreateTopicRequest(PAYMENT_FAN_OUT_TOPIC).apply {
            attributes = topicAttributes
        }
        val topicArn = amazonSNS.createTopic(topicRequest).topicArn
        val paymentFailedWithOrderEventQueueUrl =
            amazonSQS.getQueueUrl(PAYMENT_FAILED_EVENT_WITH_ORDER_QUEUE.queueName).queueUrl
        val paymentFailedWithStockEventQueueUrl =
            amazonSQS.getQueueUrl(PAYMENT_FAILED_EVENT_WITH_STOCK_QUEUE.queueName).queueUrl
        Topics.subscribeQueue(amazonSNS, amazonSQS, topicArn, paymentFailedWithOrderEventQueueUrl)
        Topics.subscribeQueue(amazonSNS, amazonSQS, topicArn, paymentFailedWithStockEventQueueUrl)

        val messageChannels = mapOf(
            STOCK_DEDUCTION_SUCCESS_EVENT_QUEUE to QueueMessageChannel(
                amazonSQS,
                stockDeductionSuccessEventQueueUrl
            ),
            PAYMENT_SUCCESS_EVENT_QUEUE to QueueMessageChannel(
                amazonSQS,
                paymentSuccessEventQueueUrl
            ),
            PAYMENT_FAILED_EVENT_SNS_TOPIC to TopicMessageChannel(amazonSNS, topicArn),
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
    fun amazonSNS(awsClientProperty: AwsClientProperty): AmazonSNSAsync =
        AmazonSNSAsyncClientBuilder.standard()
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(
                    awsClientProperty.endPoint.uri,
                    awsClientProperty.region.static,
                )
            )
            .withCredentials(
                AWSStaticCredentialsProvider(
                    BasicAWSCredentials(
                        awsClientProperty.credentials.accessKey,
                        awsClientProperty.credentials.secretKey,
                    )
                )
            )
            .build()

    @Bean
    fun amazonSQS(awsClientProperty: AwsClientProperty): AmazonSQSAsync =
        AmazonSQSAsyncClientBuilder.standard()
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(
                    awsClientProperty.endPoint.uri,
                    awsClientProperty.region.static,
                )
            )
            .withCredentials(
                AWSStaticCredentialsProvider(
                    BasicAWSCredentials(
                        awsClientProperty.credentials.accessKey,
                        awsClientProperty.credentials.secretKey,
                    )
                )
            )
            .build()

    companion object {
        const val PAYMENT_FAN_OUT_TOPIC = "payment-fan-out-topic.fifo"
    }
}
