package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.PlatformBindDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.core.dao.value.PlatformBindCo
import com.onepiece.treasure.core.dao.value.PlatformBindUo
import com.onepiece.treasure.core.model.PlatformBind
import com.onepiece.treasure.core.model.enums.Platform
import com.onepiece.treasure.core.model.enums.Status
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PlatformBindDaoImpl : BasicDaoImpl<PlatformBind>("platform_bind"), PlatformBindDao {

    override fun mapper(): (rs: ResultSet) -> PlatformBind {

        return {rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val status = rs.getString("status").let { Status.valueOf(it) }
            val openTime = rs.getTimestamp("open_time").toLocalDateTime()
            PlatformBind(id = id, clientId = clientId, platform = platform, status = status, openTime = openTime)
        }
    }

    override fun create(platformBindCo: PlatformBindCo): Boolean {
        return insert().set("client_id", platformBindCo.clientId)
                .set("platform", platformBindCo.platform)
                .executeOnlyOne()
    }

    override fun update(platformBindUo: PlatformBindUo): Boolean {
        return update().set("status", platformBindUo.status)
                .where("id", platformBindUo.id)
                .executeOnlyOne()
    }
}