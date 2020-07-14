package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PromotionCategory
import com.onepiece.gpgaming.beans.enums.PromotionPeriod
import com.onepiece.gpgaming.beans.enums.PromotionRuleType
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Promotion
import com.onepiece.gpgaming.beans.value.database.PromotionCo
import com.onepiece.gpgaming.beans.value.database.PromotionUo
import com.onepiece.gpgaming.core.dao.PromotionDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime

@Repository
class PromotionDaoImpl : BasicDaoImpl<Promotion>("promotion"), PromotionDao {

    override val mapper: (rs: ResultSet) -> Promotion
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val category = rs.getString("category").let { PromotionCategory.valueOf(it) }
            val platforms = rs.getString("platforms").split(",").map { Platform.valueOf(it) }
            val stopTime = rs.getTimestamp("stop_time")?.toLocalDateTime()
            val top = rs.getBoolean("top")
//            val icon = rs.getString("icon")
            val period = rs.getString("period").let { PromotionPeriod.valueOf(it) }
            val periodMaxPromotion = rs.getBigDecimal("period_max_promotion")
            val status = rs.getString("status").let { Status.valueOf(it) }

            val levelId = rs.getString("level_id").split(",")
                    .let {
                        if (it.isEmpty() || (it.size == 1 && it.first().isEmpty())) emptyList() else it.map { x -> x.toInt() }
                    }

            val ruleType = rs.getString("rule_type").let { PromotionRuleType.valueOf(it) }
            val ruleJson = rs.getString("rule_json")
            val sequence = rs.getInt("sequence")
            val show = rs.getBoolean("show")

            val code = rs.getString("code")

            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val updatedTime = rs.getTimestamp("updated_time").toLocalDateTime()

            Promotion(id = id, category = category, stopTime = stopTime, status = status, createdTime = createdTime,
                    clientId = clientId, top = top, updatedTime = updatedTime, platforms = platforms, levelId = levelId,
                    ruleJson = ruleJson, ruleType = ruleType, period = period, periodMaxPromotion = periodMaxPromotion,
                    sequence = sequence, show = show, code = code)
        }

    override fun create(promotionCo: PromotionCo): Int {
        return insert()
                .set("client_id", promotionCo.clientId)
                .set("category", promotionCo.category)
                .set("platforms", promotionCo.platforms.joinToString(","))
                .set("stop_time", promotionCo.stopTime)
                .set("top", promotionCo.top)
//                .set("icon", promotionCo.icon)
                .set("period", promotionCo.period)
                .set("period_max_promotion", promotionCo.periodMaxPromotion)
                .set("level_id", promotionCo.levelId.joinToString(separator = ","))
                .set("rule_json", promotionCo.ruleJson)
                .set("rule_type", promotionCo.ruleType)
                .set("sequence", promotionCo.sequence)
                .set("status", Status.Stop)
                .set("show", promotionCo.show)
                .set("code", promotionCo.code)
                .executeGeneratedKey()
    }

    override fun update(promotionUo: PromotionUo): Boolean {
        return update()
                .set("category", promotionUo.category)
                .set("platforms", promotionUo.platforms.joinToString(","))
                .setIfNull("stop_time", promotionUo.stopTime)
                .set("top", promotionUo.top)
//                .set("icon", promotionUo.icon)
                .set("period", promotionUo.period)
                .set("period_max_promotion", promotionUo.periodMaxPromotion)
                .set("status", promotionUo.status)
                .set("level_id", promotionUo.levelId?.joinToString(separator = ","))
                .set("rule_json", promotionUo.ruleJson)
                .set("updated_time", LocalDateTime.now())
                .set("sequence", promotionUo.sequence)
                .set("show", promotionUo.show)
                .where("id", promotionUo.id)
                .executeOnlyOne()
    }

//    override fun find(clientId: Int, platform: Platform): List<Promotion> {
//        return query()
//                .where("client_id", clientId)
//                .where("platform", platform)
//                .execute(mapper)
//    }
}