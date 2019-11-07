package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.PlatformBind
import com.onepiece.treasure.beans.value.database.PlatformBindCo
import com.onepiece.treasure.beans.value.database.PlatformBindUo
import com.onepiece.treasure.core.dao.PlatformBindDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PlatformBindDaoImpl : BasicDaoImpl<PlatformBind>("platform_bind"), PlatformBindDao {

    override val mapper: (rs: ResultSet) -> PlatformBind
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val username = rs.getString("username")
            val password = rs.getString("password")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            PlatformBind(id = id, clientId = clientId, platform = platform, status = status, createdTime = createdTime,
                    username = username, password = password)
        }

    override fun find(platform: Platform): List<PlatformBind> {
        return query().where("platform", platform).execute(mapper)
    }

    override fun create(platformBindCo: PlatformBindCo): Boolean {
        return insert().set("client_id", platformBindCo.clientId)
                .set("platform", platformBindCo.platform)
                .set("username", platformBindCo.username)
                .set("password", platformBindCo.password)
                .executeOnlyOne()
    }

    override fun update(platformBindUo: PlatformBindUo): Boolean {
        return update().set("status", platformBindUo.status)
                .set("username", platformBindUo.username)
                .set("password", platformBindUo.password)
                .where("id", platformBindUo.id)
                .executeOnlyOne()
    }
}