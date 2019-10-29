package com.onepiece.treasure.games

import java.time.LocalDate

interface GameOrderApi {

    fun synOrder(startDate: LocalDate, endDate: LocalDate): String

}