package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Platform
import java.math.BigDecimal
import java.time.LocalDateTime

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

        // 平台
        val platform: Platform,

        // 余额
        val balance: BigDecimal,

        // 冻结金额
        val freezeBalance: BigDecimal,

        // 当前打码量
        val currentBet: BigDecimal,

        // 需要打码量
        val demandBet: BigDecimal,

        // 优惠金额
        val giftBalance: BigDecimal,

        // 总打码量
        val totalBet: BigDecimal,

        // 总充值金额
        val totalBalance: BigDecimal,

        // 总存款次数
        val totalFrequency: Int,

        // 总优惠金额
        val totalGiftBalance: BigDecimal,

        // 进程Id
        val processId: String,

        // 创建时间
        val createdTime: LocalDateTime
)