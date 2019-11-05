package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.Platform
import java.math.BigDecimal

data class MemberPlatformDailyReportVo(

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 转入金额
        val transferIn: BigDecimal,

        // 转出金额
        val transferOut: BigDecimal,

        // 下注金额
        val bet: BigDecimal,

        // 金额  正数: 会员赢钱、厅主输钱 负数：会员输钱、厅主赢钱
        val win: BigDecimal

)

data class ClientPlatformDailyReportVo(
        val clientId: Int,

        val platform: Platform,

        val bet: BigDecimal,

        val win: BigDecimal,

        val transferIn: BigDecimal,

        val transferOut: BigDecimal
)

data class MemberDailyReportVo(

        val clientId: Int,

        val platform: Platform,

        val bet: BigDecimal,

        val win: BigDecimal,

        val transferIn: BigDecimal,

        val transferOut: BigDecimal

)

data class DepositReportVo(

        val clientId: Int,

        val memberId: Int,

        val money: BigDecimal

)


data class ClientDepositReportVo(

        val clientId: Int,


        val money: BigDecimal,

        val count: Int

)



data class WithdrawReportVo(

        val clientId: Int,

        val memberId: Int,

        val money: BigDecimal

)

data class ClientWithdrawReportVo(

        val clientId: Int,


        val money: BigDecimal,

        val count: Int

)




data class MemberTransferReportVo(

        val clientId: Int,

        val memberId: Int,

        val from: Platform,

        val to: Platform,

        val money: BigDecimal

)

data class ClientPlatformTransferReportVo(

        val clientId: Int,

        val platform: Platform,

        val from: Platform,

        val to: Platform,

        val money: BigDecimal

)

data class ClientReportVo(
        val clientId: Int,

        val bet: BigDecimal,

        val win: BigDecimal,

        val transferIn: BigDecimal,

        val transferOut: BigDecimal
)
