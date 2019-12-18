package com.onepiece.treasure.core.dao.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.model.I18nContent
import com.onepiece.treasure.beans.value.database.I18nContentCo
import com.onepiece.treasure.beans.value.database.I18nContentUo
import com.onepiece.treasure.core.dao.I18nContentDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class I18nContentDaoImpl(
        private val objectMapper: ObjectMapper
) : BasicDaoImpl<I18nContent>("i18n_content"), I18nContentDao {

    override val mapper: (rs: ResultSet) -> I18nContent
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val language = rs.getString("language").let { Language.valueOf(it) }
            val configId = rs.getInt("config_id")
            val configType = rs.getString("config_type").let { I18nConfig.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            val content = rs.getString("content")
            val iContent = when (configType) {
                I18nConfig.Announcement -> objectMapper.readValue<I18nContent.AnnouncementI18n>(content)
                I18nConfig.Banner -> objectMapper.readValue<I18nContent.BannerI18n>(content)
                I18nConfig.IndexVideo -> objectMapper.readValue<I18nContent.IndexVideoI18n>(content)
                I18nConfig.Promotion -> objectMapper.readValue<I18nContent.PromotionI18n>(content)
                I18nConfig.IndexSport -> objectMapper.readValue<I18nContent.IndexSportI18n>(content)
            }

            I18nContent(id = id, clientId = clientId, language = language, configId = configId,configType = configType,
                    createdTime = createdTime, content= iContent)
        }

    override fun create(i18nContentCo: I18nContentCo): Int {
        return insert().set("client_id", i18nContentCo.clientId)
                .set("content", objectMapper.writeValueAsString(i18nContentCo.content))
                .set("language", i18nContentCo.language)
                .set("config_id", i18nContentCo.configId)
                .set("config_type", i18nContentCo.configType)
                .executeGeneratedKey()
    }

    override fun update(i18nContentUo: I18nContentUo): Boolean {
        return update()
                .set("content", objectMapper.writeValueAsString(i18nContentUo.content))
                .where("id", i18nContentUo.id)
                .executeOnlyOne()

    }

    override fun getConfigType(clientId: Int, configType: I18nConfig): List<I18nContent> {
        return query()
                .where("client_id", clientId)
                .where("config_type", configType)
                .execute(mapper)
    }
}