package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.RiskLevel
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.SaleScope
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime


sealed class MemberValue {

    data class AnalysisData(

            val id: Int,

            val v: String

    )


}


data class MemberQuery(

        val bossId: Int? = null,

        val clientId: Int? = null,

        val agentId: Int? = null,

        val role: Role? = null,

        val username: String? = null,

        val usernames: List<String>? = null,

        val name: String? = null,

        val registerIp: String? = null,

        val phone: String? = null,

        val status: Status? = null,

        val levelId: Int? = null,

        val ids: List<Int>? = null,

        val promoteCode: String? = null,

        val startTime: LocalDateTime? = null,

        val endTime: LocalDateTime? = null
)

data class MemberCo(

        // bossId
        val bossId: Int,

        // 厅主Id
        val clientId: Int,

        // 代理Id
        val agentId: Int,

        // 电销Id
        val saleId: Int = -1,

        // 营销Id
        val marketId: Int = -1,

        // 电销类型
        val saleScope: SaleScope = SaleScope.System,

        // 介绍Id
        val introduceId: Int = -1,

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

        // 推广来源(现在就是用户Id)
        val promoteCode: String?,

        // 是否正式
        val formal: Boolean,

        // 注册ip
        val registerIp: String,

        // 风险等级
        val riskLevel: RiskLevel = RiskLevel.None,

        // 邮箱
        val email: String?,

        // 出生日期
        val birthday: LocalDate?
)

data class MemberUo(

        val id: Int,

        // 电销Id
        val saleId: Int? = null,

        // 姓名
        val name: String? = null,

        // 风险等级
        val riskLevel: RiskLevel? = null,

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

        // vip id
        val vipId: Int? = null,

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
        val agencyMonthFee: BigDecimal? = null,

        // 邮箱
        val email: String? = null,

        // 出生日期
        val birthday: LocalDate? = null,

        // 出生日期
        val address: String? = null,

        // 身份证
        val idCard: String? = null


)


