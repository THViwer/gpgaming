package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.WebSiteDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.beans.value.database.WebSiteCo
import com.onepiece.treasure.beans.value.database.WebSiteUo
import com.onepiece.treasure.beans.model.WebSite
import com.onepiece.treasure.beans.enums.Status
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class WebSiteDaoImpl : BasicDaoImpl<WebSite>("web_site"), WebSiteDao {

    override fun mapper(): (rs: ResultSet) -> WebSite {
        return { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val domain = rs.getString("domain")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            WebSite(id = id, clientId = clientId, domain = domain, status = status, createdTime = createdTime)
        }
    }

    override fun create(webSiteCo: WebSiteCo): Boolean {
        return insert().set("client_id", webSiteCo.clientId)
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