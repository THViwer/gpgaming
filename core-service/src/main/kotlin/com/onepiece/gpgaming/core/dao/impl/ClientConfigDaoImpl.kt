package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.ShowPosition
import com.onepiece.gpgaming.beans.model.ClientConfig
import com.onepiece.gpgaming.beans.value.internet.web.ClientConfigValue
import com.onepiece.gpgaming.core.dao.ClientConfigDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ClientConfigDaoImpl: BasicDaoImpl<ClientConfig>("client_config"), ClientConfigDao {

    override val mapper: (rs: ResultSet) -> ClientConfig
        get() = { rs ->

            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val title = rs.getString("title")
            val keywords = rs.getString("keywords")
            val description = rs.getString("description")
            val liveChatId = rs.getString("live_chat_id")
            val liveChatTab = rs.getBoolean("live_chat_tab")
            val googleStatisticsId = rs.getString("google_statistics_id")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val facebookTr = rs.getString("facebook_tr")
            val facebookShowPosition = rs.getString("facebook_show_position")
                    .let { ShowPosition.valueOf(it) }
            val asgContent = rs.getString("asg_content")
            val registerMessageTemplate = rs.getString("register_message_template")
            ClientConfig(id = id, clientId = clientId, keywords = keywords, description = description, createdTime = createdTime,
                    title = title, liveChatId = liveChatId, googleStatisticsId = googleStatisticsId, facebookTr = facebookTr,
                    liveChatTab = liveChatTab, asgContent = asgContent, facebookShowPosition = facebookShowPosition,
                    registerMessageTemplate = registerMessageTemplate)

        }

    override fun create(configUo: ClientConfigValue.ClientConfigUo): Boolean {
        return  insert()
                .set("client_id", configUo.clientId)
                .set("title", configUo.title)
                .set("keywords", configUo.keywords)
                .set("description", configUo.description)
                .set("live_chat_id", configUo.liveChatId)
                .set("live_chat_tab", configUo.liveChatTab)
                .set("google_statistics_id", configUo.googleStatisticsId)
                .set("facebook_tr", configUo.facebookTr)
                .set("facebook_show_position", configUo.facebookShowPosition)
                .executeOnlyOne()
    }

    override fun update(configUo: ClientConfigValue.ClientConfigUo): Boolean {
        return update()
                .set("title", configUo.title)
                .set("keywords", configUo.keywords)
                .set("description", configUo.description)
                .set("live_chat_id", configUo.liveChatId)
                .set("live_chat_tab", configUo.liveChatTab)
                .set("google_statistics_id", configUo.googleStatisticsId)
                .set("facebook_tr", configUo.facebookTr)
                .set("facebook_show_position", configUo.facebookShowPosition)
                .set("asg_content", configUo.asgContent)
                .where("client_id", configUo.clientId)
                .executeOnlyOne()
    }
}