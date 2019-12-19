package com.onepiece.gpgaming.games.value

import java.math.BigDecimal
import java.time.LocalDate


data class ReportVo(

        val day: LocalDate,

        val win: BigDecimal,

        val bet: BigDecimal
)