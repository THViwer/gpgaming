package com.onepiece.treasure.beans.value.database

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
        val transferOut: BigDecimal
)

data class ClientPlatformDailyReportVo(
        val clientId: Int,

        val platform: Platform,

        val transferIn: BigDecimal,

        val transferOut: BigDecimal
)

data class MemberDailyReportVo(

        val clientId: Int,

        val platform: Platform,

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

        val from: Platform,

        val to: Platform,

        val money: BigDecimal

)

data class ClientReportVo(
        val clientId: Int,

        val transferIn: BigDecimal,

        val transferOut: BigDecimal
)
