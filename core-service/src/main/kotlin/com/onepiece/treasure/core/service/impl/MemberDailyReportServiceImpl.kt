package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.model.MemberDailyReport
import com.onepiece.treasure.beans.value.database.MemberReportQuery
import com.onepiece.treasure.beans.value.internet.web.MemberDailyReportVo
import com.onepiece.treasure.core.dao.MemberDailyReportDao
import com.onepiece.treasure.core.service.MemberDailyReportService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MemberDailyReportServiceImpl(
        private val memberDailyReportDao: MemberDailyReportDao
) : MemberDailyReportService {

    override fun create(reports: List<MemberDailyReport>) {
        return memberDailyReportDao.create(reports)
    }

    override fun query(query: MemberReportQuery): List<MemberDailyReport> {
        return memberDailyReportDao.query(query)
    }

}