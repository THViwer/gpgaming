package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Platform
import java.math.BigDecimal

data class PlatformMemberCo(

        // 平台
        val platform: Platform,

        // 会员 Id
        val memberId: Int,

        // 用户名
        val username: String,

        // 密码
        val password: String
)

data class PlatformMemberTransferUo(
        val id: Int,

        // 金额
        val money: BigDecimal,

        // 赠送金额
        val giftBalance: BigDecimal,

        // 需要打码量
        val demandBet: BigDecimal


)

data class PlatformMemberBetUo(
        val memberId: Int,

        val platform: Platform,

//         余额
//        val balance: BigDecimal,

        // 打码量
        val bet: BigDecimal

)