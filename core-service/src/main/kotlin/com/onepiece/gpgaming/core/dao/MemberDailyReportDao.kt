package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery

interface MemberDailyReportDao  {

    fun create(reports: List<MemberDailyReport>)

    fun query(query: MemberReportQuery): List<MemberDailyReport>

}