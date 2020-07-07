package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.model.AppVersion
import com.onepiece.gpgaming.core.dao.AppVersionDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class AppVersionDaoImpl: BasicDaoImpl<AppVersion>("app_version"), AppVersionDao {

    override val mapper: (rs: ResultSet) -> AppVersion
        get() = { rs ->

            val id = rs.getInt("id")
            val mainClientId = rs.getInt("main_client_id")
            val launch = rs.getString("launch").let { LaunchMethod.valueOf(it) }
            val url = rs.getString("url")
            val version = rs.getString("version")
            val content = rs.getString("content")
            val constraint = rs.getBoolean("constraint")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            AppVersion(id = id, mainClientId = mainClientId, launch = launch, url = url, version = version, content = content,
                    constraint = constraint, createdTime = createdTime)

        }

    override fun getVersions(mainClientId: Int): List<AppVersion> {
        return query()
                .where("main_client_id", mainClientId)
                .execute(mapper)
    }
}