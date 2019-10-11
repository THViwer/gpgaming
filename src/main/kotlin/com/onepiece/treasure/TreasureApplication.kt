package com.onepiece.treasure

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TreasureApplication

fun main(args: Array<String>) {
	runApplication<TreasureApplication>(*args)
}
