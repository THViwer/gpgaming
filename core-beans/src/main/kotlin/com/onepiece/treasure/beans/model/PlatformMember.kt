package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Platform
import java.math.BigDecimal
import java.time.LocalDateTime

data class PlatformMember(

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 平台
        val platform: Platform,

        // 会员Id
        val memberId: Int,

        // 用户名
        val username: String,

        // 密码
        val password: String,

        // 总打码量
        val totalBet: BigDecimal,

        // 总盈利
        val totalWin: BigDecimal,

        // 总充值金额
        val totalAmount: BigDecimal,

        // 总出款金额
        val totalTransferOutAmount: BigDecimal,

        // 总优惠金额
        val totalPromotionAmount: BigDecimal,

        // 创建时间
        val createdTime: LocalDateTime,


        // 下面属性随时会变 是做记录用
        // 参加优惠活动Id
        val joinPromotionId: Int?,

        // 当前打码量
        val currentBet: BigDecimal,

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