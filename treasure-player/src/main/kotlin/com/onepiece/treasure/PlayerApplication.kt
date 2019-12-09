package com.onepiece.treasure

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync


@SpringBootApplication
@EnableAsync
open class PlayerApplication

fun main() {
    runApplication<PlayerApplication>()
}