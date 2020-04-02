package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery
import com.onepiece.gpgaming.core.dao.MemberDailyReportDao
import com.onepiece.gpgaming.core.service.MemberDailyReportService
import org.springframework.stereotype.Service

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

    override fun queryBackwater(current: Int, size: Int): List<MemberDailyReport> {
        return memberDailyReportDao.queryBackwater(current = current, size = size)
    }

    override fun updateBackwater(ids: List<Int>) {
        memberDailyReportDao.updateBackwater(ids)
    }
}