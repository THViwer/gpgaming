package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.ApplyState
import java.time.LocalDateTime

data class AgentApply(

        // id
        val id: Int,

        // bossId
        val bossId: Int,

        // clientId
        val clientId: Int,

        // 会员Id
        val agentId: Int,

        // 状态
        val state: ApplyState,

        // 备注
        val remark: String,

        // 审核时间
        val checkTime:  LocalDateTime?,

        // 创建时间
        val createdTime:  LocalDateTime

)