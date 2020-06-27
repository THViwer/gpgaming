package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.TraceType
import java.time.LocalDateTime

/**
 * 会员追踪链路
 */
data class MemberTraceLog(

        // id
        val id: Int,

        // bossId
        val bossId: Int,

        // 业主Id
        val clientId: Int,

        // 销售员Id
        val saleId: Int,

        // 会员Id
        val memberId: Int,

        // 追踪类型
        val type: TraceType,

        // 备注
        val remark: String,

        // 创建时间
        val createdTime: LocalDateTime

)