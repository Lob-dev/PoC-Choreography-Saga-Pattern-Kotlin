package com.study.stock.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "cloud.aws")
data class AwsSqsClientProperty(
    val region: Region,
    val credentials: Credentials,
    val endPoint: EndPoint,
)

data class Region(val static: String, val auto: Boolean)
data class Credentials(val accessKey: String, val secretKey: String)
data class EndPoint(val uri: String)
