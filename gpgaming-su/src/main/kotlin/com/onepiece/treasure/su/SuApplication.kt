package com.onepiece.treasure.su

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("com.onepiece.treasure")
open class SuApplication


fun main() {
    runApplication<SuApplication>()
}