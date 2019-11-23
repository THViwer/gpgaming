package com.onepiece.treasure.core.dao.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.treasure.beans.enums.*
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Promotion
import com.onepiece.treasure.beans.model.PromotionRules
import com.onepiece.treasure.beans.value.database.PromotionCo
import com.onepiece.treasure.beans.value.database.PromotionUo
import com.onepiece.treasure.core.dao.PromotionDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime

@Repository
class PromotionDaoImpl(
        private val objectMapper: ObjectMapper
) : BasicDaoImpl<Promotion>("promotion"), PromotionDao {

    override val mapper: (rs: ResultSet) -> Promotion
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val category = rs.getString("category").let { PromotionCategory.valueOf(it) }
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val stopTime = rs.getTimestamp("stop_time")?.toLocalDateTime()
            val top = rs.getBoolean("top")
            val icon = rs.getString("icon")
            val status = rs.getString("status").let { Status.valueOf(it) }

            val levelId = rs.getInt("level_id")
            val ruleType = rs.getString("rule_type").let { PromotionRuleType.valueOf(it) }
            val ruleJson = rs.getString("rule_json")

            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val updatedTime = rs.getTimestamp("updated_time").toLocalDateTime()

            Promotion(id = id, category = category, stopTime = stopTime, icon = icon, status = status, createdTime = createdTime,
                    clientId = clientId, top = top, updatedTime = updatedTime, platform = platform, levelId = levelId,
                    ruleJson = ruleJson, ruleType = ruleType)
        }

    override fun create(promotionCo: PromotionCo): Int {
        return insert()
                .set("client_id", promotionCo.clientId)
                .set("category", promotionCo.category)
                .set("platform", promotionCo.platform)
                .set("stop_time", promotionCo.stopTime)
                .set("top", promotionCo.top)
                .set("icon", promotionCo.icon)
                .set("level_id", promotionCo.levelId)
                .set("rule_json", promotionCo.ruleJson)
                .set("rule_type", promotionCo.ruleType)
                .set("status", Status.Normal)
                .executeGeneratedKey()
    }

    override fun update(promotionUo: PromotionUo): Boolean {
        return update()
                .set("category", promotionUo.category)
                .set("stop_time", promotionUo.stopTime)
                .set("top", promotionUo.top)
                .set("icon", promotionUo.icon)
                .set("status", promotionUo.status)
                .set("level_id", promotionUo.levelId)
                .set("rule_json", promotionUo.ruleJson)
                .set("updated_time", LocalDateTime.now())
                .where("id", promotionUo.id)
                .executeOnlyOne()
    }

    override fun find(clientId: Int, platform: Platform): List<Promotion> {
        return query()
                .where("client_id", clientId)
                .where("platform", platform)
                .execute(mapper)
    }
}