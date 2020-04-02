package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery

interface MemberDailyReportService  {

    fun create(reports: List<MemberDailyReport>)

    fun query(query: MemberReportQuery): List<MemberDailyReport>

    fun queryBackwater(current: Int, size: Int): List<MemberDailyReport>

    fun updateBackwater(ids: List<Int>)

}