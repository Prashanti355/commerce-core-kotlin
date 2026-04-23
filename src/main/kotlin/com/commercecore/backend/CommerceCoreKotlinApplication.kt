package com.commercecore.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CommerceCoreKotlinApplication

fun main(args: Array<String>) {
    runApplication<CommerceCoreKotlinApplication>(*args)
}