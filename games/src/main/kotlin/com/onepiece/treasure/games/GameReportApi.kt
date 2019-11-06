package com.onepiece.treasure.games

import com.onepiece.treasure.games.value.ReportVo
import java.time.LocalDate

interface GameReportApi {

    fun memberReport(username: String, startDate: LocalDate, endDate: LocalDate): List<ReportVo>

    fun clientReport(startDate: LocalDate, endDate: LocalDate): List<ReportVo>

}