package com.onepiece.treasure.task

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAsync
@EnableScheduling
@ComponentScan("com.onepiece.treasure")
open class TaskApplication

fun main() {
    runApplication<TaskApplication>()
}