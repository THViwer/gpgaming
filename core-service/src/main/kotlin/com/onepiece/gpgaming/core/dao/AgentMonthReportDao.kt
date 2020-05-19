package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.AgentMonthReport
import com.onepiece.gpgaming.beans.value.database.AgentReportValue

interface AgentMonthReportDao {

    fun create(data: List<AgentMonthReport>)

    fun query(query: AgentReportValue.AgentMonthQuery): List<AgentMonthReport>

    fun commissions(): List<AgentMonthReport>

    fun executionCommission(ids: List<Int>)
}