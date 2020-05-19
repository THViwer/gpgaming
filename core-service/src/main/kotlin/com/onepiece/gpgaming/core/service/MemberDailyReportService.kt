package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery
import com.onepiece.gpgaming.beans.value.database.MemberReportValue

interface MemberDailyReportService  {

    fun create(reports: List<MemberDailyReport>)

    fun total(query: MemberReportQuery): MemberReportValue.MemberReportTotal

    fun query(query: MemberReportQuery): List<MemberDailyReport>

    fun queryRebate(current: Int, size: Int): List<MemberDailyReport>

    fun updateRebate(ids: List<Int>)

}