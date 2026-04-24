package com.commercecore.backend

import com.commercecore.backend.config.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
class CommerceCoreKotlinApplication

fun main(args: Array<String>) {
    runApplication<CommerceCoreKotlinApplication>(*args)
}