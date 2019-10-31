package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.PromotionRuleCategory
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.PromotionRule
import com.onepiece.treasure.beans.value.database.PromotionRuleCo
import com.onepiece.treasure.beans.value.database.PromotionRuleUo
import com.onepiece.treasure.core.dao.PromotionRuleDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PromotionRuleDaoImpl : BasicDaoImpl<PromotionRule>("promotion_rule"), PromotionRuleDao {

    override val mapper: (rs: ResultSet) -> PromotionRule
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val category = rs.getString("category").let { PromotionRuleCategory.valueOf(it) }
            val levelId = rs.getInt("level_id")
            val ruleJson = rs.getString("rule_json")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            PromotionRule(id = id, clientId = clientId, platform = platform, category = category, levelId = levelId,
                    ruleJson = ruleJson, status = status, createdTime = createdTime)

        }

    override fun create(promotionRuleCo: PromotionRuleCo): Boolean {
        return insert()
                .set("client_id", promotionRuleCo.clientId)
                .set("platform", promotionRuleCo.platform)
                .set("category", promotionRuleCo.platform)
                .set("levelId", promotionRuleCo.levelId)
                .set("rule_json", promotionRuleCo.ruleJson)
                .set("status", Status.Normal)
                .executeOnlyOne()

    }

    override fun update(promotionRuleUo: PromotionRuleUo): Boolean {
        return update()
                .set("platform", promotionRuleUo.platform)
                .set("category", promotionRuleUo.platform)
                .set("levelId", promotionRuleUo.levelId)
                .set("rule_json", promotionRuleUo.ruleJson)
                .set("status", promotionRuleUo.status)
                .set("id", promotionRuleUo.id)
                .executeOnlyOne()
    }
}