package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.WalletEvent
import java.math.BigDecimal


data class WalletQuery(

        // 厅主Id
        val clientId: Int,

        // 用户Id
        val memberId: Int?,

        // 最小余额
        val minBalance: BigDecimal?,

        // 最大余额
        val maxBalance: BigDecimal?,

        // 最小总充值金额
        val minTotalDepositBalance: BigDecimal?,

        // 最大总充值金额
        val maxTotalDepositBalance: BigDecimal?,

        // 最小总取款金额
        val minTotalWithdrawBalance: BigDecimal?,

        // 最大总取款金额
        val maxTotalWithdrawBalance: BigDecimal?,

        // 最小充值次数
        val minTotalDepositFrequency: Int?,

        // 最大充值次数
        val maxTotalDepositFrequency: Int?,

        // 最小取款次数
        val minTotalWithdrawFrequency: Int?,

        // 最小取款次数
        val maxTotalWithdrawFrequency: Int?


)

data class WalletCo(

        val clientId: Int,

        val memberId: Int
)

data class WalletUo(

        val clientId: Int,

        val waiterId: Int?,

        val memberId: Int,

        // 金额
        val money: BigDecimal,

        // 赠送金额
        val giftBalance: BigDecimal = BigDecimal.ZERO,

        // 事件Id
        val eventId: String?,

        // 钱包事件
        val event: WalletEvent,

        // 备注
        val remarks: String
)

data class WalletDepositUo(
        val id: Int,

        val processId: String,

        val money: BigDecimal
)

data class WalletFreezeUo(
        val id: Int,

        val processId: String,

        val money: BigDecimal
)

data class WalletWithdrawUo(
        val id: Int,

        val processId: String,

        val money: BigDecimal
)

data class WalletTransferInUo(
        val id: Int,

        val processId: String,

        val money: BigDecimal
)

data class WalletTransferOutUo(

        val id: Int,

        val processId: String,

        val money: BigDecimal,



        val giftMoney: BigDecimal
)