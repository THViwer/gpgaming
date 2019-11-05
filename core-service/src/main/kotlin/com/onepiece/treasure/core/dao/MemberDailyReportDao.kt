package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.MemberDailyReport
import com.onepiece.treasure.beans.value.database.MemberReportQuery

interface MemberDailyReportDao  {

    fun create(reports: List<MemberDailyReport>)

    fun query(query: MemberReportQuery): List<MemberDailyReport>

}