package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.PromotionRuleCategory
import com.onepiece.treasure.beans.enums.Status

class PromotionRuleCo(

        // 厅主Id
        val clientId: Int,

        // 平台
        val platform: Platform,

        // 充值送类目
        val category: PromotionRuleCategory,

        // 优惠层级Id 如果为null则是全部
        val levelId: Int?,

        // 规则
        val ruleJson: String

)
data class PromotionRuleUo(

        // id
        val id: Int,

        // 平台
        val platform: Platform? = null,

        // 充值送类目
        val category: PromotionRuleCategory? = null,

        // 优惠层级Id 如果为null则是全部
        val levelId: Int? = null,

        // 规则
        val ruleJson: String? = null,

        // 状态
        val status: Status? = null
)