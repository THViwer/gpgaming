package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.model.MemberPlatformDailyReport
import com.onepiece.treasure.beans.value.database.MemberReportQuery
import com.onepiece.treasure.beans.value.internet.web.ClientPlatformDailyReportVo
import com.onepiece.treasure.beans.value.internet.web.MemberPlatformDailyReportVo
import com.onepiece.treasure.core.dao.MemberPlatformDailyReportDao
import com.onepiece.treasure.core.service.MemberPlatformDailyReportService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MemberPlatformDailyReportServiceImpl(
        private val memberPlatformDailyReportDao: MemberPlatformDailyReportDao
) : MemberPlatformDailyReportService {

    override fun create(reports: List<MemberPlatformDailyReport>) {
        return memberPlatformDailyReportDao.create(reports)
    }

    override fun query(query: MemberReportQuery): List<MemberPlatformDailyReport> {
        return memberPlatformDailyReportDao.query(query)
    }

    override fun report(startDate: LocalDate, endDate: LocalDate): List<MemberPlatformDailyReportVo> {
        return memberPlatformDailyReportDao.report(startDate, endDate)
    }

    override fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientPlatformDailyReportVo> {
        return memberPlatformDailyReportDao.reportByClient(startDate, endDate)
    }
}