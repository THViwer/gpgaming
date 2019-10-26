package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Platform
import java.math.BigDecimal
import java.time.LocalDateTime

data class PlatformMember(

        // id
        val id: Int,

        // 平台
        val platform: Platform,

        // 会员Id
        val memberId: Int,

        // 用户名
        val username: String,

        // 密码
        val password: String,

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

        // 总优惠金额
        val totalGiftBalance: BigDecimal,

        // 创建时间
        val createdTime: LocalDateTime

)