package com.onepiece.gpgaming.beans.value.database

sealed class AgentReportValue {

    data class AgentDailyQuery(

            val bossId: Int,

            val agentId: Int?
    )

    data class AgentMonthQuery(

            val bossId: Int,

            val agentId: Int
    )


}