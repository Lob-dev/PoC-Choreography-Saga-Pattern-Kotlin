package com.study.stock.config

import com.study.common.communication.FeignErrorTranslator
import feign.Contract
import feign.codec.ErrorDecoder
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.cloud.openfeign.FeignFormatterRegistrar
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar

@EnableFeignClients
@Configuration
class FeignDefaultConfiguration {

    /**
     * ref: https://github.com/OpenFeign/feign/pull/543/commits/4beb8b005baaee5e0874ab2947ab0a6898485983
     * fix OpenFeign 9.6.0 (Feign Default Contract)
     * Use @RequestLine
     */
    @Bean
    protected fun defaultContract(): Contract = Contract.Default()

    /**
     * ref: https://techblog.woowahan.com/2630/
     * #Java8 이상 에서 필수 설정
     */
    @Bean
    protected fun localDateFeignFormatterRegister(): FeignFormatterRegistrar =
        FeignFormatterRegistrar {
            DateTimeFormatterRegistrar().apply {
                setUseIsoFormat(true)
                registerFormatters(it)
            }
        }

    @Bean
    protected fun errorDecoder(): ErrorDecoder =
        FeignErrorTranslator()
}
