package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.BannerType
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.Banner
import com.onepiece.treasure.beans.value.database.BannerCo
import com.onepiece.treasure.beans.value.database.BannerUo
import com.onepiece.treasure.core.dao.BannerDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime

@Repository
class BannerDaoImpl: BasicDaoImpl<Banner>("banner"), BannerDao {

    override val mapper: (rs: ResultSet) -> Banner
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val icon = rs.getString("icon")
            val touchIcon = rs.getString("touch_icon")
            val type = rs.getString("type").let { BannerType.valueOf(it) }
            val link = rs.getString("link")
            val order = rs.getInt("order")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val updatedTime = rs.getTimestamp("updated_time").toLocalDateTime()
            Banner(id = id, clientId = clientId, icon = icon, touchIcon = touchIcon, type = type, link = link, order = order,
                    status = status, createdTime = createdTime, updatedTime = updatedTime)
        }

    override fun create(bannerCo: BannerCo): Boolean {
        return insert()
                .set("client_id", bannerCo.clientId)
                .set("icon", bannerCo.icon)
                .set("touch_icon", bannerCo.touchIcon)
                .set("type", bannerCo.type)
                .set("link", bannerCo.link)
                .set("status", Status.Normal)
                .executeOnlyOne()
    }

    override fun update(bannerUo: BannerUo): Boolean {
        return update()
                .set("icon", bannerUo.icon)
                .set("touch_icon", bannerUo.touchIcon)
                .set("type", bannerUo.type)
                .set("link", bannerUo.link)
                .set("status", bannerUo.status)
                .set("updated_time", LocalDateTime.now())
                .where("id", bannerUo.id)
                .executeOnlyOne()

    }
}