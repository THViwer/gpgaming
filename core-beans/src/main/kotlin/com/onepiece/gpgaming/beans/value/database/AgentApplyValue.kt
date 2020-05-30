package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.ApplyState

sealed class AgentApplyValue {

    data class ApplyCo(

            val bossId: Int,

            val clientId: Int,

            val agentId: Int,

            val state: ApplyState,

            val remark: String?
    )

    data class ApplyUo(

            val id: Int,

            val state: ApplyState,

            val remark: String
    )

    data class ApplyQuery(

            val bossId: Int,

            val clientId: Int,

            val state: ApplyState,

            val agentId: Int? = null

    )

}