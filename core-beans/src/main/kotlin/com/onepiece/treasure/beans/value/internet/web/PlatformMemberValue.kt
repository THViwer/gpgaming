package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.Platform
import java.math.BigDecimal

data class PlatformMemberVo(

        // id
        val id: Int,

        // 用户Id
        val memberId: Int,

        // 平台
        val platform: Platform,

        // 平台账号
        val platformUsername: String,

        // 平台密码
        val platformPassword: String
)

data class PlatformMemberDetailVo(

        // id
        val id: Int,

        // 用户Id
        val memberId: Int,

        // 平台
        val platform: Platform,

        // 平台账号
        val platformUsername: String,

        // 总打码量
        val totalBet: BigDecimal,

        // 总盈利
        val totalWin: BigDecimal,

        // 总充值金额
        val totalAmount: BigDecimal,

        // 总出款金额
        val totalTransferOutAmount: BigDecimal,

        // 总优惠金额
        val totalPromotionAmount: BigDecimal
)