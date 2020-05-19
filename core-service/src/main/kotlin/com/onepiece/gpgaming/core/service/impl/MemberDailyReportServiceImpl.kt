package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery
import com.onepiece.gpgaming.beans.value.database.MemberReportValue
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

    override fun total(query: MemberReportQuery): MemberReportValue.MemberReportTotal {
        return memberDailyReportDao.total(query)
    }

    override fun query(query: MemberReportQuery): List<MemberDailyReport> {
        return memberDailyReportDao.query(query)
    }

    override fun queryRebate(current: Int, size: Int): List<MemberDailyReport> {
        return memberDailyReportDao.queryRebate(current = current, size = size)
    }

    override fun updateRebate(ids: List<Int>) {
        memberDailyReportDao.updateRebate(ids)
    }
}