package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


sealed class MemberValue {

    data  class AnalysisData(

            val id: Int,

            val v: String

    )


}


data class MemberQuery(

        val bossId: Int?,

        val clientId: Int?,

        val agentId: Int?,

        val role: Role?,

        val username: String?,

        val name: String?,

        val phone: String?,

        val status: Status?,

        val levelId: Int?,

        val ids: List<Int>? = null,

        val promoteCode: String?,

        val startTime: LocalDateTime?,

        val endTime: LocalDateTime?
)

data class MemberCo(

        // bossId
        val bossId: Int,

        // 厅主Id
        val clientId: Int,

        // 代理Id
        val agentId: Int,

        // 角色
        val role: Role,

        // 用户名
        val username: String,

        // 姓名
        val name: String,

        // 手机号
        val phone: String,

        // 密码
        val password: String,

        // 安全密码
        val safetyPassword: String,

        // 等级Id
        val levelId: Int,

        // 推广来源
        val promoteCode: String?,

        // 是否正式
        val formal: Boolean
)

data class MemberUo(

        val id: Int,

        // 姓名
        val name: String? = null,

        // 手机号
        val phone: String? = null,

        // 旧密码
        val oldPassword: String? = null,

        // 首充优惠
        val firstPromotion: Boolean? = null,

        // 密码
        val password: String? = null,

        // 旧安全密码
        val oldSafetyPassword: String? = null,

        // 安全密码
        val safetyPassword: String? = null,

        // 等级Id
        val levelId: Int? = null,

        // 自动转账
        val autoTransfer: Boolean? = null,

        // 状态
        val status: Status? = null,

        // 登陆Ip
        val loginIp: String? = null,

        // 登陆时间
        val loginTime: LocalDateTime? = null,

        // 是否正式
        val formal: Boolean? = null,

        // 代理费
        val agencyMonthFee: BigDecimal? = null
)


