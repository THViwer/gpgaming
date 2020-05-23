package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

sealed class AgentValue {

    data  class AgentRegisterReq(

            // 用户名
            val username: String,

            // 密码
            val password: String,

            // 名字
            val name: String,

            // 电话
            val phone: String,

            // 推广code
            val code: String?
    )

    data class AgentLoginResp(

            // 姓名
            val name: String,

            // token
            val token: String,

            // 推广码
            val promoteCode: String,

            // 推广地址
            val urls: List<PromoteVo>

    )

    data class PromoteVo(

            // 国家
            val country: Country,

            // 推广链接
            val promoteURL: String,

            // 手机推广地址
            val mobilePromoteURL: String

    )

    data class AgentCo(

            // 用户Id
            val bossId: Int,

            // 用户名
            val username: String,

            // 密码
            val password: String,

            // 状态
            val status: Status,

            // 推广code
            val code: String,

            // 登陆时间
            val loginTime: LocalDateTime
    )

    data class AgentUo(

            // Id
            val id: Int,

            // 密码
            val password: String?,

            // 状态
            val status: Status?,

            // 推广code
            val code: String?,

            // 登陆时间
            val loginTime: LocalDateTime?
    )

    data class AgentInfo(

            // 余额
            val balance: BigDecimal,

            // 下级代理总数
            val subAgentCount: Int,

            // 会员总数
            val memberCount:  Int,

            // 当月会员佣金
            val memberCommission: BigDecimal,

            // 当月下级代理佣金
            val subAgentCommission: BigDecimal,

            // 代理月费
            val agencyMonthFee: BigDecimal
    )

    data class SubAgentVo(

            // 姓名
            val username: String,

            // 手机
            val phone: String,

            // 会员总数
            val memberCount: Int,

            // 是否是正式
            val formal: Boolean,

            // 创建时间
            val createdTime: LocalDateTime

    )

    data class AgentCommissionVo(

            // 日期
            val day: LocalDate,

            // 会员充值
            val totalDeposit: BigDecimal,

            // 会员取款
            val totalWithdraw: BigDecimal,

            // 当前总下注
            val totalBet: BigDecimal,

            // 当前顾客盈利
            val totalMWin: BigDecimal,

            // 下级代理佣金
            val subAgentCommission: BigDecimal,

            // 会员佣金
            val memberCommission: BigDecimal,

            // 总返水
            val totalRebate: BigDecimal,

            // 总优惠金额
            val totalPromotion: BigDecimal,

            // 新增会员数
            val newMemberCount: Int
    )

    data class MemberCommissionVo(

            // 用户名
            val username: String,

            // 总下注
            val totalBet: BigDecimal,

            // 顾客盈利
            val totalMWin: BigDecimal,

            // 返水
            val totalRebate: BigDecimal,

            // 总优惠金额
            val totalPromotion: BigDecimal
    )


}