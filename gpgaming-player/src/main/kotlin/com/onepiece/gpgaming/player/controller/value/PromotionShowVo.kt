package com.onepiece.gpgaming.player.controller.value

import java.math.BigDecimal

data class PromotionShowVo(

        // 优惠Id
        val promotionId: Int,

        // 最小转账金额
        val minAmount: BigDecimal,

        // 最大转账金额
        val maxAmount: BigDecimal,

        // 优惠标题
        val title: String,

        // 规则
        val ruleJson: String

)