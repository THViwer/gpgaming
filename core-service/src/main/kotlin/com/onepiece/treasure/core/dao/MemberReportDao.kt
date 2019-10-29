package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.MemberReport
import com.onepiece.treasure.beans.value.database.MemberReportQuery
import com.onepiece.treasure.core.dao.basic.BasicDao

interface MemberReportDao: BasicDao<MemberReport> {

    fun creates(reports: List<MemberReport>)

    fun query(query: MemberReportQuery): List<MemberReport>

}