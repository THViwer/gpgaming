package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Platform
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

        val count: Int,

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

        val money: BigDecimal,

        val count: Int

)

data class ClientWithdrawReportVo(

        val clientId: Int,

        val money: BigDecimal,

        val count: Int

)


data class MemberTransferPlatformReportVo(

        val clientId: Int,

        val memberId: Int,

        val from: Platform,

        val to: Platform,

        val money: BigDecimal
) {

    val platform: Platform
        get() = if (from == Platform.Center) to else from

}

data class MemberTransferReportVo(

        val clientId: Int,

        val memberId: Int,

        val money: BigDecimal,

        val promotionAmount: BigDecimal

)

data class ClientTransferPlatformReportVo(

        val clientId: Int,


        val from: Platform,

        val to: Platform,

        val money: BigDecimal,

        val promotionAmount: BigDecimal

) {

    val platform: Platform
        get() = if (from == Platform.Center) to else from

}

data class TransferOrderReportVo(
        val clientId: Int,

        val platform: Platform,

        val promotionId: Int,

        val promotionAmount: BigDecimal

)

data class ClientTransferReportVo(
        val clientId: Int,

        val transferIn: BigDecimal,

        val transferOut: BigDecimal,

        val promotionAmount: BigDecimal

)
