package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.PromotionRule
import com.onepiece.treasure.beans.value.database.PromotionRuleCo
import com.onepiece.treasure.beans.value.database.PromotionRuleUo
import com.onepiece.treasure.core.dao.basic.BasicDao

interface PromotionRuleDao: BasicDao<PromotionRule> {

    fun create(promotionRuleCo: PromotionRuleCo): Boolean

    fun update(promotionRuleUo: PromotionRuleUo): Boolean


}