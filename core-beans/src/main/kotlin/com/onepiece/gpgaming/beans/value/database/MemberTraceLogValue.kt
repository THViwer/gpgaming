package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.TraceType

sealed class MemberTraceLogValue {

    data class MemberTraceLogCo(
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
            val remark: String
    )

    data class MemberTraceLogQuery(

            // bossId
            val bossId: Int,

            // 业主Id
            val clientId: Int,

            // 销售员Id
            val saleId: Int?,

            // 会员Id
            val memberId: Int?
    )

}