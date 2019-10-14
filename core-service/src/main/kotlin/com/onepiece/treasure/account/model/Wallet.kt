package com.onepiece.treasure.account.model

import java.math.BigDecimal

/**
 * 主钱包
 */
data class Wallet(

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 余额
        val balance: BigDecimal,

        // 总充值金额
        val totalBalance: BigDecimal,

        // 总优惠金额
        val giftBalance: BigDecimal,

        // 创建时间
        val createdTime: Int
)