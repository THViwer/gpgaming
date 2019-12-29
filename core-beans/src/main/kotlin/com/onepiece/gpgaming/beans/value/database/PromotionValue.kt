package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PromotionCategory
import com.onepiece.gpgaming.beans.enums.PromotionPeriod
import com.onepiece.gpgaming.beans.enums.PromotionRuleType
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime

data class PromotionCo(

        // 厅主Id
        val clientId: Int,

        // 类型
        val category: PromotionCategory,

        // 平台
        val platforms: List<Platform>,

        // 结束时间, 如果为null 则无限时间
        val stopTime: LocalDateTime?,

        // 优惠周期
        val period: PromotionPeriod,

        // 每天的最大优惠
        val periodMaxPromotion: BigDecimal,

        // 是否置顶
        val top: Boolean,

        // 层级Id
        val levelId: Int?,

        // 优惠类型
        val ruleType: PromotionRuleType,

        // 路由规则json
        val ruleJson: String

)

data class PromotionUo(
        // id
        val id: Int,

        // 优惠类型
        val category: PromotionCategory? = null,

        // 平台
        val platforms: List<Platform>,

        // 结束时间, 如果为null 则无限时间
        val stopTime: LocalDateTime?,

        // 每天的最大优惠
        val periodMaxPromotion: BigDecimal,

        // 优惠周期
        val period: PromotionPeriod?,

        // 是否置顶
        val top: Boolean? = null,

        // 活动状态
        val status: Status? = null,

        // 层级Id
        val levelId: Int?,

        // 路由规则json
        val ruleJson: String?

)