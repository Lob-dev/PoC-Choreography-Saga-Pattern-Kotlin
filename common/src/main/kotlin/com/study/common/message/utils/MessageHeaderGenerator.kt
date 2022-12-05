package com.study.common.message.utils

import io.awspring.cloud.messaging.core.SqsMessageHeaders
import java.util.*

object MessageHeaderGenerator {
    fun generate(groupId: String, messageId: String): Map<String, String> = mapOf(
        SqsMessageHeaders.SQS_GROUP_ID_HEADER to groupId,
        SqsMessageHeaders.SQS_DEDUPLICATION_ID_HEADER to messageId,
    )
}
