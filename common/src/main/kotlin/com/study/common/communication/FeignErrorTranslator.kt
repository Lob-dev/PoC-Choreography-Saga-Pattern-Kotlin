package com.study.common.communication

import feign.Response
import feign.codec.ErrorDecoder
import mu.KLogger
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

class FeignErrorTranslator : ErrorDecoder {
    private val logger: KLogger = KotlinLogging.logger { }

    override fun decode(methodKey: String, response: Response): Exception {
        val errorResponse: String = response.body().asInputStream().use {
            BufferedReader(InputStreamReader(it))
                .lines().parallel().collect(Collectors.joining("\n"))
        }
        logger.info { "FeignErrorTranslator errorResponse = $errorResponse" }

        return when (response.status()) {
            else -> ResponseStatusException(HttpStatus.valueOf(response.status()))
        }
    }
}
