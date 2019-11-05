package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.Platform
import java.math.BigDecimal
import java.time.LocalDate

class MemberPlatformReportWebVo(

        // 日期
        val day: LocalDate,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 用户名
        val username: String,

        // 平台Id
        val platform: Platform,

        // 转入金额
        val transferIn: BigDecimal,

        // 转出金额
        val transferOut: BigDecimal,

        // 下注金额
        val bet: BigDecimal,

        // 金额  正数: 会员赢钱、厅主输钱 负数：会员输钱、厅主赢钱
        val win: BigDecimal
)


data class MemberReportWebVo(

        // 日期
        val day: LocalDate,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 用户名
        val username :String,

        // 下注金额
        val bet: BigDecimal,

        // 金额  正数: 会员赢钱、厅主输钱 负数：会员输钱、厅主赢钱
        val win: BigDecimal,

        // 转入金额
        val transferIn: BigDecimal,

        // 转出金额
        val transferOut: BigDecimal,

        // 充值金额
        val depositMoney: BigDecimal,

        // 取款金额
        val withdrawMoney: BigDecimal

)