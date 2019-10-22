package com.onepiece.treasure.web

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("com.onepiece.treasure")
open class WebApplication

fun main() {
    runApplication<WebApplication>()
}