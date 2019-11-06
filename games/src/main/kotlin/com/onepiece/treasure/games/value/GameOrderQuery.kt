package com.onepiece.treasure.games.value

import java.time.LocalDate

data class GameOrderQuery (

        val startDate: LocalDate,

        val endDate: LocalDate,

        val clientId: Int,

        val memberId: Int

)