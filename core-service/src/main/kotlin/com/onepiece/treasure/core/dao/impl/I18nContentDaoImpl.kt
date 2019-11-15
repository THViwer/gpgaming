package com.onepiece.treasure.core.dao.impl

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
class I18nContentDaoImpl : BasicDaoImpl<I18nContent>("i18n_content"), I18nContentDao {

    override val mapper: (rs: ResultSet) -> I18nContent
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val title = rs.getString("title")
            val content = rs.getString("content")
            val synopsis = rs.getString("synopsis")
            val language = rs.getString("language").let { Language.valueOf(it) }
            val configId = rs.getInt("config_id")
            val configType = rs.getString("config_type").let { I18nConfig.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            I18nContent(id = id, clientId = clientId, title = title, content = content, synopsis = synopsis, language = language, configId = configId,
                    configType = configType, createdTime = createdTime)
        }


    override fun create(i18nContentCo: I18nContentCo): Int {

        return insert().set("client_id", i18nContentCo.clientId)
                .set("title", i18nContentCo.title)
                .set("content", i18nContentCo.content)
                .set("synopsis", i18nContentCo.synopsis)
                .set("language", i18nContentCo.language)
                .set("config_id", i18nContentCo.configId)
                .set("config_type", i18nContentCo.configType)
                .executeGeneratedKey()
    }

    override fun update(i18nContentUo: I18nContentUo): Boolean {
        return update().set("title", i18nContentUo.title)
                .set("content", i18nContentUo.content)
                .set("synopsis", i18nContentUo.synopsis)
                .set("language", i18nContentUo.language)
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