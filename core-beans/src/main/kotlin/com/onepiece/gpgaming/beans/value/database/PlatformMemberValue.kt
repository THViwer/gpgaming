package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PromotionCategory
import java.math.BigDecimal

data class PlatformMemberCo(

        // 厅主Id
        val clientId: Int,

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

        // 下面属性随时会变 是做记录用
        // 参加优惠活动Id
        val joinPromotionId: Int?,

        // 优惠活动的优惠信息
        val promotionJson: String?,

        // 参加活动的平台
//        val joinPlatform: Platform?,

        // 优惠活动列表
        val platforms: List<Platform>,

        // 优惠类型
        val category: PromotionCategory,

        // 当前打码量
        val currentBet: BigDecimal,

        // 优惠计算金额
        val promotionPreMoney: BigDecimal,

        // 需要打码量
        val requirementBet: BigDecimal,

        // 优惠金额
        val promotionAmount: BigDecimal,

        // 转到平台金额
        val transferAmount: BigDecimal,

        // 转出到中心平台
        val requirementTransferOutAmount: BigDecimal,

        // 当金额小于时 不需要打码量和转出金额限制
        val ignoreTransferOutAmount: BigDecimal
)

data class PlatformMemberBetUo(
        val memberId: Int,

        val platform: Platform,

//         余额
//        val balance: BigDecimal,

        // 打码量
        val bet: BigDecimal

)