package com.onepiece.gpgaming.core.dao.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.value.database.I18nContentCo
import com.onepiece.gpgaming.beans.value.database.I18nContentUo
import com.onepiece.gpgaming.core.dao.I18nContentDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
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

            val contentJson = rs.getString("content_json")
            val status = rs.getString("status").let { Status.valueOf(it) }


            I18nContent(id = id, clientId = clientId, language = language, configId = configId, configType = configType,
                    createdTime = createdTime, contentJson = contentJson, status = status)
        }

    override fun create(i18nContentCo: I18nContentCo): Int {
        return insert().set("client_id", i18nContentCo.clientId)
                .set("content_json", objectMapper.writeValueAsString(i18nContentCo.content))
                .set("language", i18nContentCo.language)
                .set("config_id", i18nContentCo.configId)
                .set("config_type", i18nContentCo.configType)
                .executeGeneratedKey()
    }

    override fun update(i18nContentUo: I18nContentUo): Boolean {
        return update()
                .set("content_json", objectMapper.writeValueAsString(i18nContentUo.content))
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