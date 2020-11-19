package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.AppDown
import com.onepiece.gpgaming.beans.value.database.AppDownValue
import com.onepiece.gpgaming.core.dao.AppDownDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class AppDownDaoImpl : BasicDaoImpl<AppDown>("app_down"), AppDownDao {

    override val mapper: (rs: ResultSet) -> AppDown
        get() = { rs ->

            val id = rs.getInt("id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val icon = rs.getString("icon")
            val iosPath = rs.getString("ios_path")
            val androidPath = rs.getString("android_path")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            AppDown(id = id, platform = platform, iosPath = iosPath, androidPath = androidPath, status = status, createdTime = createdTime,
                    icon = icon)
        }

    override fun create(appDown: AppDown): Boolean {
        return insert()
                .set("icon", appDown.icon)
                .set("platform", appDown.platform)
                .set("ios_path", appDown.iosPath)
                .set("android_path", appDown.androidPath)
                .set("status", appDown.status)
                .executeOnlyOne()
    }

    override fun update(update: AppDownValue.Update): Boolean {
        return update()
                .set("status", update.status)
                .set("icon", update.icon)
                .setIfNull("ios_path", update.iosPath)
                .setIfNull("android_path", update.androidPath)
                .where("id", update.id)
                .executeOnlyOne()
    }
}