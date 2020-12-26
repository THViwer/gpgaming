package com.onepiece.gpgaming.player.controller.value

import com.onepiece.gpgaming.beans.enums.Platform
import java.math.BigDecimal
import java.time.LocalDateTime

data class PromotionHistoryVo(

        // 订单Id
        val orderId: String,

        // 创建日期
        val createdTime: LocalDateTime,

        //优惠标题
        val promotionTitle: String,

        // 转账金额
        val amount: BigDecimal,

        // 优惠金额
        val promotionAmount: BigDecimal,

        // 转出平台
        val from: Platform,

        // 转入平台
        val to: Platform,

        // 需要打码量
        val requirementBet: BigDecimal,

        // 当前打码(只有status=Process时才有值  其它值为-1)
        val currentBet: BigDecimal,

        // 目前状态
        val status: PromotionStatus


) {

    enum class PromotionStatus {
        Done,

        Process
    }
}