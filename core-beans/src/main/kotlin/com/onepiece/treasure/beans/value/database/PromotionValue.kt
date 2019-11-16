package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.PromotionCategory
import com.onepiece.treasure.beans.enums.PromotionRuleType
import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

data class PromotionCo(

        // 厅主Id
        val clientId: Int,

        // 类型
        val category: PromotionCategory,

        // 平台
        val platform: Platform,

        // 结束时间, 如果为null 则无限时间
        val stopTime: LocalDateTime?,

        // 是否置顶
        val top: Boolean,

        // 图标
        val icon: String,

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

        // 结束时间, 如果为null 则无限时间
        val stopTime: LocalDateTime?,

        // 是否置顶
        val top: Boolean? = null,

        // 图标
        val icon: String? = null,

        // 活动状态
        val status: Status? = null,

        // 层级Id
        val levelId: Int?,

        // 路由规则json
        val ruleJson: String?

)