package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Banner
import com.onepiece.gpgaming.beans.value.database.BannerCo
import com.onepiece.gpgaming.beans.value.database.BannerUo
import com.onepiece.gpgaming.core.dao.BannerDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime

@Repository
class BannerDaoImpl: BasicDaoImpl<Banner>("banner"), BannerDao {

    override val mapper: (rs: ResultSet) -> Banner
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val type = rs.getString("type").let { BannerType.valueOf(it) }
            val platformCategory = rs.getString("platform_category")?.let {
                PlatformCategory.valueOf(it)
            }
            val link = rs.getString("link")
            val order = rs.getInt("order")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val updatedTime = rs.getTimestamp("updated_time").toLocalDateTime()
            Banner(id = id, clientId = clientId, type = type, link = link, order = order, platformCategory = platformCategory,
                    status = status, createdTime = createdTime, updatedTime = updatedTime)
        }

    override fun create(bannerCo: BannerCo): Int {
        return insert()
                .set("client_id", bannerCo.clientId)
//                .set("icon", bannerCo.icon)
//                .set("touch_icon", bannerCo.touchIcon)
                .set("order", bannerCo.order)
                .set("type", bannerCo.type)
                .set("platform_category", bannerCo.platformCategory)
                .set("link", bannerCo.link)
                .set("status", Status.Stop)
                .executeGeneratedKey()
    }

    override fun update(bannerUo: BannerUo): Boolean {
        return update()
                .set("type", bannerUo.type)
                .set("platform_category", bannerUo.platformCategory)
                .set("link", bannerUo.link)
                .set("order", bannerUo.order)
                .set("status", bannerUo.status)
                .set("updated_time", LocalDateTime.now())
                .where("id", bannerUo.id)
                .executeOnlyOne()

    }
}