package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.Seo
import com.onepiece.gpgaming.beans.value.internet.web.SeoValue
import com.onepiece.gpgaming.core.dao.SeoDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class SeoDaoImpl: BasicDaoImpl<Seo>("seo"), SeoDao {

    override val mapper: (rs: ResultSet) -> Seo
        get() = { rs ->

            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val title = rs.getString("title")
            val keywords = rs.getString("keywords")
            val description = rs.getString("description")
            val liveChatId = rs.getString("live_chat_id")
            val googleStatisticsId = rs.getString("google_statistics_id")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val facebookTr = rs.getString("facebook_tr")
            Seo(id = id, clientId = clientId, keywords = keywords, description = description, createdTime = createdTime,
                    title = title, liveChatId = liveChatId, googleStatisticsId = googleStatisticsId, facebookTr = facebookTr)

        }

    override fun create(seoUo: SeoValue.SeoUo): Boolean {
        return  insert()
                .set("client_id", seoUo.clientId)
                .set("title", seoUo.title)
                .set("keywords", seoUo.keywords)
                .set("description", seoUo.description)
                .set("live_chat_id", seoUo.liveChatId)
                .set("google_statistics_id", seoUo.googleStatisticsId)
                .set("facebook_tr", seoUo.facebookTr)
                .executeOnlyOne()
    }

    override fun update(seoUo: SeoValue.SeoUo): Boolean {
        return update()
                .set("title", seoUo.title)
                .set("keywords", seoUo.keywords)
                .set("description", seoUo.description)
                .set("live_chat_id", seoUo.liveChatId)
                .set("google_statistics_id", seoUo.googleStatisticsId)
                .set("facebook_tr", seoUo.facebookTr)
                .where("client_id", seoUo.clientId)
                .executeOnlyOne()
    }
}