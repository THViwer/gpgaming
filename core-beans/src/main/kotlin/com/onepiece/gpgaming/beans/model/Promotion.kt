package com.onepiece.gpgaming.beans.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PromotionCategory
import com.onepiece.gpgaming.beans.enums.PromotionPeriod
import com.onepiece.gpgaming.beans.enums.PromotionRuleType
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.value.database.PlatformMemberTransferUo
import com.onepiece.gpgaming.utils.JacksonUtil
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
        val platforms: List<Platform>,

        // 结束时间, 如果为null 则无限时间
        val stopTime: LocalDateTime?,

        // 优惠规则
        val ruleType: PromotionRuleType,

        // 优惠层级Id 如果为null则是全部
        val levelId: List<Int>,

        // 优惠周期
        val period: PromotionPeriod,

        // 周期内最大金额
        val periodMaxPromotion: BigDecimal,

        // 规则
        val ruleJson: String,

        // 是否置顶
        val top: Boolean,

        // 活动状态
        val status: Status,

        // 序列
        val sequence: Int,

        // 是否前台显示
        val show: Boolean,

        // 创建时间
        val createdTime: LocalDateTime,

        // 更新时间
        val updatedTime: LocalDateTime

) {

    fun <T> getPromotionRuleCondition(mapper: ObjectMapper, clz: Class<T>):  T{
        return mapper.readValue(ruleJson, clz)
    }

    // 优惠规则
    val rule: PromotionRules.Rule
        @JsonIgnore
        get() {
            return when (ruleType) {
                PromotionRuleType.Bet -> JacksonUtil.objectMapper.readValue<PromotionRules.BetRule>(ruleJson)
                PromotionRuleType.Withdraw -> JacksonUtil.objectMapper.readValue<PromotionRules.WithdrawRule>(ruleJson)
            }
        }


    /**
     * 获得转账的文字描述
     */
    fun getPromotionIntroduction(amount: BigDecimal, platformBalance: BigDecimal, overPromotionAmount: BigDecimal, language: Language): String {

        return when (this.ruleType) {
            PromotionRuleType.Bet -> {
                val betRule = this.rule as PromotionRules.BetRule
                val promotionAmount = getPromotionAmount(amount = amount, platformBalance = platformBalance, overPromotionAmount = overPromotionAmount)
                val requirementBet = (amount.plus(promotionAmount).plus(platformBalance)).multiply(betRule.betMultiple).setScale(2, 2)


                when (language) {
                    Language.CN -> "转账:$amount, 平台剩余:${platformBalance},优惠:${promotionAmount}, 实际到账:${amount.plus(promotionAmount).plus(platformBalance)}, 打码量达到${requirementBet}可取出"
                    else ->   "Transfer:$amount, Main Wallet:${platformBalance},Promotion Bonus:${promotionAmount}, Total Amount:${amount.plus(promotionAmount).plus(platformBalance)}, Turnover reach ${requirementBet} to transfer out"
                }

            }
            PromotionRuleType.Withdraw -> {
                val withdrawRule = this.rule as PromotionRules.WithdrawRule
                val promotionAmount = getPromotionAmount(amount = amount, platformBalance = platformBalance, overPromotionAmount = overPromotionAmount)
                val requirementTransferOutAmount = (amount.plus(promotionAmount).plus(platformBalance)).multiply(withdrawRule.transferMultiplied).setScale(2,2)


                when (language) {
                    Language.CN -> "转账:$amount, 平台剩余:${platformBalance},优惠:${promotionAmount}, 实际到账:${amount.plus(promotionAmount).plus(platformBalance)}, 游戏金额${requirementTransferOutAmount}可取出"
                    else -> "Transfer:$amount, Main Wallet:${platformBalance},Promotion Bonus:${promotionAmount}, Total Amount:${amount.plus(promotionAmount).plus(platformBalance)}, Game balance reach ${requirementTransferOutAmount} to transfer out"
                }
            }
        }

    }


    /**
     * 获得优惠金额
     */
    private fun getPromotionAmount(amount: BigDecimal, platformBalance: BigDecimal, overPromotionAmount: BigDecimal): BigDecimal{

        if (this.rule.minAmount.toDouble() > amount.toDouble()) return BigDecimal.ZERO
        if (this.rule.maxAmount.toDouble() < amount.toDouble()) return BigDecimal.ZERO

        val promotionAmount = when (this.ruleType) {
            PromotionRuleType.Bet -> {
                val betRule = this.rule as PromotionRules.BetRule
                (amount.plus(platformBalance)).multiply(betRule.promotionProportion)
            }
            PromotionRuleType.Withdraw -> {
                val withdrawRule = this.rule as PromotionRules.WithdrawRule
                (amount.plus(platformBalance)).multiply(withdrawRule.promotionProportion)
            }
        }

        return promotionAmount
                .let {
                    if (it > this.rule.maxPromotionAmount) this.rule.maxPromotionAmount else it
                }
                .let {
                    if (it > overPromotionAmount) overPromotionAmount else it
                }
                .setScale(2, 2)
    }

    /**
     * 获得活动的更新对象
     */
    fun getPlatformMemberTransferUo(
            platformMemberId: Int,
            amount: BigDecimal,
            platformBalance: BigDecimal,
            overPromotionAmount: BigDecimal,
            promotionId: Int
    ): PlatformMemberTransferUo {

        val promotionPreMoney = amount.plus(platformBalance)

        val init = PlatformMemberTransferUo(id = platformMemberId, joinPromotionId = promotionId, currentBet = BigDecimal.ZERO, requirementBet = BigDecimal.ZERO,
                promotionAmount = BigDecimal.ZERO, transferAmount = amount, requirementTransferOutAmount = BigDecimal.ZERO, ignoreTransferOutAmount = BigDecimal.ZERO,
                promotionJson = null, platforms = platforms, category = category, promotionPreMoney = promotionPreMoney)

        return when (this.ruleType) {
            PromotionRuleType.Bet -> {
                val betRule = this.rule as PromotionRules.BetRule

                val promotionAmount = this.getPromotionAmount(amount = amount, platformBalance = platformBalance, overPromotionAmount = overPromotionAmount)
                val requirementBet = (amount.plus(promotionAmount).plus(platformBalance)).multiply(betRule.betMultiple)

                init.copy(currentBet = BigDecimal.ZERO, requirementBet = requirementBet, ignoreTransferOutAmount = betRule.ignoreTransferOutAmount,
                        promotionAmount = promotionAmount, promotionJson = ruleJson)

            }
            PromotionRuleType.Withdraw -> {

                val withdrawRule = this.rule as PromotionRules.WithdrawRule

                val promotionAmount = this.getPromotionAmount(amount = amount, platformBalance = platformBalance, overPromotionAmount = overPromotionAmount)
                val requirementTransferOutAmount = (amount.plus(promotionAmount).plus(platformBalance)).multiply(withdrawRule.transferMultiplied)

                init.copy(currentBet = BigDecimal.ZERO, requirementTransferOutAmount = requirementTransferOutAmount, ignoreTransferOutAmount = withdrawRule.ignoreTransferOutAmount,
                        promotionAmount = promotionAmount, promotionJson = ruleJson)

            }
        }
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

        // 最大优惠金额
        val maxPromotionAmount: BigDecimal
    }

    data class BetRule(
            override val minAmount: BigDecimal,
            override val maxAmount: BigDecimal,
            override val ignoreTransferOutAmount: BigDecimal,
            override val maxPromotionAmount: BigDecimal,

            // 打码倍数
            val betMultiple: BigDecimal,

            // 赠送比例
            val promotionProportion: BigDecimal


    ): Rule

    data class WithdrawRule(

            override val minAmount: BigDecimal,
            override val maxAmount: BigDecimal,
            override val ignoreTransferOutAmount: BigDecimal,
            override val maxPromotionAmount: BigDecimal,

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
