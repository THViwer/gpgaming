package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.WalletEvent
import java.math.BigDecimal

data class WalletCo(

        val clientId: Int,

        val memberId: Int
)

data class WalletUo(

        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 进程Id
        val processId: String,

        // 金额
        val money: BigDecimal,

        // 冻结金额
        val freezeMoney: BigDecimal,

        // 下注金额
        val bet: BigDecimal,

        // 添加余额
        val addBalance: BigDecimal,

        // 赠送金额
        val giftMoney: BigDecimal,

        // 钱包事件
        val event: WalletEvent,

        // 备注
        val remarks: String
)