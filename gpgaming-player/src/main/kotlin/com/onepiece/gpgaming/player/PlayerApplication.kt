package com.onepiece.gpgaming.player

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableAsync


@SpringBootApplication
@EnableAsync
@ComponentScan("com.onepiece.gpgaming")
open class PlayerApplication

fun main() {
    runApplication<PlayerApplication>()
}