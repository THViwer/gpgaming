package com.onepiece.treasure.games.old

import com.onepiece.treasure.games.value.ClientAuthVo
import com.onepiece.treasure.games.value.ReportVo
import java.time.LocalDate

interface GameReportApi {

    fun memberReport(clientAuthVo: ClientAuthVo? = null, username: String, startDate: LocalDate, endDate: LocalDate): List<ReportVo>

    fun clientReport(clientAuthVo: ClientAuthVo? = null, startDate: LocalDate, endDate: LocalDate): List<ReportVo>

}