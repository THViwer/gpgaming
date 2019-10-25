package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Platform
import java.math.BigDecimal

/**
 * 平台钱包
 */
data class PlatformWallet(

        // 用户Id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 余额(参考，不实时)
        val balance: BigDecimal,

        // 充值金额
        val totalBalance: BigDecimal,

        // 优惠金额
        val giftBalance: BigDecimal,

        // 总打码量
        val totalStreamFlow: BigDecimal,

        // 当前打码量
        val currentStreamFlow: BigDecimal,

        // 平台
        val platform: Platform

)