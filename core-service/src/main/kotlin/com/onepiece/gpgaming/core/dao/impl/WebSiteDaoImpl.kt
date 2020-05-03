package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.WebSite
import com.onepiece.gpgaming.beans.value.database.WebSiteCo
import com.onepiece.gpgaming.beans.value.database.WebSiteUo
import com.onepiece.gpgaming.core.dao.WebSiteDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class WebSiteDaoImpl : BasicDaoImpl<WebSite>("web_site"), WebSiteDao {

    override val mapper: (rs: ResultSet) -> WebSite
        get() = { rs ->
            val id = rs.getInt("id")
            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val domain = rs.getString("domain")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            WebSite(id = id, clientId = clientId, domain = domain, status = status, createdTime = createdTime,
                    bossId = bossId)
        }

    override fun create(webSiteCo: WebSiteCo): Boolean {
        return insert()
                .set("boss_id", webSiteCo.bossId)
                .set("client_id", webSiteCo.clientId)
                .set("domain", webSiteCo.domain)
                .executeOnlyOne()

    }

    override fun update(webSiteUo: WebSiteUo): Boolean {
        return update().set("domain", webSiteUo.domain)
                .set("status", webSiteUo.status)
                .where("id", webSiteUo.id)
                .executeOnlyOne()
    }
}