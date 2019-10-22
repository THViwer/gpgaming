package com.onepiece.treasure.web

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
@ComponentScan("com.onepiece.treasure")
@EnableWebSecurity
open class WebApplication

fun main() {
    runApplication<WebApplication>()
}