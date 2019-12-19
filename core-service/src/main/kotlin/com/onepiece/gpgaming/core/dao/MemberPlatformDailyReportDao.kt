package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.MemberPlatformDailyReport
import com.onepiece.gpgaming.beans.value.database.ClientPlatformDailyReportVo
import com.onepiece.gpgaming.beans.value.database.MemberPlatformDailyReportVo
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery
import com.onepiece.gpgaming.core.dao.basic.BasicDao
import java.time.LocalDate

interface MemberPlatformDailyReportDao: BasicDao<MemberPlatformDailyReport> {

    fun create(reports: List<MemberPlatformDailyReport>)

    fun query(query: MemberReportQuery): List<MemberPlatformDailyReport>

    fun report(startDate: LocalDate, endDate: LocalDate): List<MemberPlatformDailyReportVo>

    fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientPlatformDailyReportVo>

}