package com.onepiece.gpgaming.beans.value.database

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
            val agentId: Int? = null
    )


}