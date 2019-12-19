package com.onepiece.gpgaming.su

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("com.onepiece.gpgaming")
open class SuApplication


fun main() {
    runApplication<SuApplication>()
}