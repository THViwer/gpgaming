package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.AdvertType
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.Advert
import com.onepiece.treasure.beans.value.database.AdvertCo
import com.onepiece.treasure.beans.value.database.AdvertUo
import com.onepiece.treasure.core.dao.AdvertDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class AdvertDaoImpl: BasicDaoImpl<Advert>("advert"), AdvertDao {

    override val mapper: (rs: ResultSet) -> Advert
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val icon = rs.getString("icon")
            val touchIcon = rs.getString("touch _icon")
            val position = rs.getString("position").let { AdvertType.valueOf(it) }
            val link = rs.getString("link")
            val order = rs.getInt("order")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            Advert(id = id, clientId = clientId, icon = icon, touchIcon = touchIcon, position = position, link = link, order = order,
                    status = status, createdTime = createdTime)
        }

    override fun create(advertCo: AdvertCo): Boolean {
        return insert()
                .set("client_id", advertCo.clientId)
                .set("icon", advertCo.icon)
                .set("touch_icon", advertCo.touchIcon)
                .set("position", advertCo.position)
                .set("link", advertCo.link)
                .set("status", Status.Normal)
                .executeOnlyOne()
    }

    override fun update(advertUo: AdvertUo): Boolean {
        return update()
                .set("icon", advertUo.icon)
                .set("touch_icon", advertUo.touchIcon)
                .set("position", advertUo.position)
                .set("link", advertUo.link)
                .set("status", advertUo.status)
                .where("id", advertUo.id)
                .executeOnlyOne()

    }
}