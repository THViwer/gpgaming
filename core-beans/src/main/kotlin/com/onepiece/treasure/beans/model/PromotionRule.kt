package com.onepiece.treasure.beans.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.PromotionRuleCategory
import com.onepiece.treasure.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 优惠活动规则
 */
data class PromotionRule(

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 平台
        val platform: Platform,

        // 充值送类目
        val category: PromotionRuleCategory,

        // 优惠层级Id 如果为null则是全部
        val levelId: Int?,

        // 规则
        val ruleJson: String,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime

) {

    fun <T> getPromotioRuleCondition(mapper: ObjectMapper, clz: Class<T>):  T{
        return mapper.readValue(ruleJson, clz)
    }

}

sealed class PromotionRules{

    data class BetRule(

            // 打码倍数
            val betMultiple: BigDecimal,

            // 最小金额
            val minMoney: BigDecimal,

            // 赠送比例
            val giftProportion: BigDecimal
    )

    data class TransferRule(

            // 最小转账金额
            val minTransferAmount: BigDecimal,

            // 最大转账金额
            val maxTransferAmount: BigDecimal,

            // 取款倍数
            val transferMultiplied: BigDecimal

    )

}


