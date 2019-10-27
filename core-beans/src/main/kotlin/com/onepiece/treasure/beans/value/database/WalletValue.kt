package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.WalletEvent
import java.math.BigDecimal

data class WalletCo(

        val clientId: Int,

        val memberId: Int
)

data class WalletUo(

        val clientId: Int,

        val memberId: Int,

        // 金额
        val money: BigDecimal,

        // 赠送金额
        val giftBalance: BigDecimal = BigDecimal.ZERO,

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