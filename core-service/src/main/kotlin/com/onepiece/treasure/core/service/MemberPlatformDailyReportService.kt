package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.MemberPlatformDailyReport
import com.onepiece.treasure.beans.value.database.MemberReportQuery
import com.onepiece.treasure.beans.value.internet.web.ClientPlatformDailyReportVo
import com.onepiece.treasure.beans.value.internet.web.MemberPlatformDailyReportVo
import java.time.LocalDate

interface MemberPlatformDailyReportService {

    fun create(reports: List<MemberPlatformDailyReport>)

    fun query(query: MemberReportQuery): List<MemberPlatformDailyReport>

    fun report(startDate: LocalDate, endDate: LocalDate): List<MemberPlatformDailyReportVo>

    fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientPlatformDailyReportVo>


}