package com.onepiece.gpgaming.beans.value.database

import java.time.LocalDate

sealed class AgentReportValue {

    data class AgentDailyQuery(

            val bossId: Int,

            val agentId: Int?
    )

    data class AgentMonthQuery(

            // bossId
            val bossId: Int,

            // 业主Id
            val clientId: Int,

            // 上级代理Id
            val superiorAgentId: Int? = null,

            // 代理Id
            val agentId: Int? = null,

            // 查询开始时间
            val startDate: LocalDate? = null,

            // 查询结束时间
            val endDate: LocalDate? = null
    )


}