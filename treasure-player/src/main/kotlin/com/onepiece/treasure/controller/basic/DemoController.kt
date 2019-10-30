package com.onepiece.treasure.controller.basic

import com.onepiece.treasure.games.GameOrderApi
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/demo")
class DemoController(
        private val jokerGameOrderApi: GameOrderApi
) {


    @GetMapping("/order")
    fun order() {
        val endDate = LocalDateTime.now()
        val startDate = endDate.minusHours(1)
        val x = jokerGameOrderApi.synOrder(startTime = startDate, endTime = endDate)
        println(x)
    }


}