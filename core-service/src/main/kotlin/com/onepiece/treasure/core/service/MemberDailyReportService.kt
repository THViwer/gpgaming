package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.MemberDailyReport
import com.onepiece.treasure.beans.value.database.MemberReportQuery

interface MemberDailyReportService  {

    fun create(reports: List<MemberDailyReport>)

    fun query(query: MemberReportQuery): List<MemberDailyReport>

}