package com.onepiece.treasure.games

import java.time.LocalDateTime

interface GameOrderApi {

    fun synOrder(startTime: LocalDateTime, endTime: LocalDateTime): String

}