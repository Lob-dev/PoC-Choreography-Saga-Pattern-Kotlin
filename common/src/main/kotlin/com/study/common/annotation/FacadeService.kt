package com.study.common.annotation

import org.springframework.stereotype.Service

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Service
annotation class FacadeService {}
