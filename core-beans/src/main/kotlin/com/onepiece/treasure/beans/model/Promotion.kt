package com.onepiece.treasure.beans.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.PromotionCategory
import com.onepiece.treasure.beans.enums.PromotionRuleType
import com.onepiece.treasure.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 优惠活动
 */
data class Promotion (

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 优惠类型
        val category: PromotionCategory,

        // 平台
        val platform: Platform,

        // 结束时间, 如果为null 则无限时间
        val stopTime: LocalDateTime?,

        // 优惠规则
        val ruleType: PromotionRuleType,

        // 优惠层级Id 如果为null则是全部
        val levelId: Int?,

        // 优惠规则
        val rule: PromotionRules.Rule,

        // 规则
        val ruleJson: String,

        // 是否置顶
        val top: Boolean,

        // 图标
        val icon: String,

        // 活动状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime,

        // 更新时间
        val updatedTime: LocalDateTime

) {

    fun <T> getPromotioRuleCondition(mapper: ObjectMapper, clz: Class<T>):  T{
        return mapper.readValue(ruleJson, clz)
    }

}

sealed class PromotionRules{

    interface Rule {
        // 最小转账金额
        val minAmount: BigDecimal

        // 最大转账金额
        val maxAmount: BigDecimal

        // 最小转出金额和可转入金额
        val ignoreTransferOutAmount: BigDecimal
    }

    data class BetRule(
            override val minAmount: BigDecimal,
            override val maxAmount: BigDecimal,
            override val ignoreTransferOutAmount: BigDecimal,

            // 打码倍数
            val betMultiple: BigDecimal,

            // 赠送比例
            val promotionProportion: BigDecimal


    ): Rule

    data class WithdrawRule(

            override val minAmount: BigDecimal,
            override val maxAmount: BigDecimal,
            override val ignoreTransferOutAmount: BigDecimal,

            // 取款倍数
            val transferMultiplied: BigDecimal,

            // 赠送比例
            val promotionProportion: BigDecimal

    ): Rule

}
//
//fun main() {
//    val betRule = PromotionRules.BetRule(minAmount = BigDecimal.ZERO, maxAmount = BigDecimal.valueOf(99999), betMultiple = BigDecimal(2),
//            promotionProportion = BigDecimal(0.5), ignoreTransferOutAmount = BigDecimal(10))
//    println(jacksonObjectMapper().writeValueAsString(betRule))
//
//
//    val withdrawRule = PromotionRules.WithdrawRule(minAmount = BigDecimal.ZERO, maxAmount = BigDecimal.valueOf(99999), transferMultiplied = BigDecimal(3),
//            promotionProportion = BigDecimal(0.5), ignoreTransferOutAmount = BigDecimal(10))
//    println(jacksonObjectMapper().writeValueAsString(withdrawRule))
//
//    "{\"minAmount\":0,\"maxAmount\":99999,\"betMultiple\":2,\"promotionProportion\":0.5,\"ignoreTransferOutAmount\":10}"
//    "{\"minAmount\":0,\"maxAmount\":99999,\"transferMultiplied\":3,\"promotionProportion\":0.5,\"ignoreTransferOutAmount\":10}"
//
//
//    """
//        {
//          "category": "VIP",
//          "i18nContent": {
//            "content": "this is my first promotion",
//            "language": "EN",
//            "synopsis": "hi, promotion",
//            "title": "first promotion"
//          },
//          "icon": "string",
//          "platform": "Joker",
//          "top": true,
//          "promotionRuleVo": {
//            "ruleJson": "{\"minAmount\":0,\"maxAmount\":99999,\"transferMultiplied\":3,\"promotionProportion\":0.5,\"ignoreTransferOutAmount\":10}\n",
//            "ruleType": "Withdraw"
//          }
//        }
//    """.trimIndent()
//}
