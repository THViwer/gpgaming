package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Blog
import com.onepiece.gpgaming.beans.value.database.BlogValue
import com.onepiece.gpgaming.core.dao.BlogDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class BlogDaoImpl: BasicDaoImpl<Blog>("blog"),BlogDao {

    override val mapper: (rs: ResultSet) -> Blog
        get() = { rs ->

            val id = rs.getInt("id")
            val title = rs.getString("title")
            val headImg = rs.getString("head_img")
            val sort = rs.getInt("sort")
            val platform = rs.getString("platform")
                    .let { Platform.valueOf(it) }
            val status = rs.getString("status")
                    .let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time")
                    .toLocalDateTime()

            Blog(id = id, title = title, headImg = headImg, sort = sort, platform = platform, status = status, createdTime = createdTime)
        }

    override fun list(clientId: Int): List<Blog> {
        return all(clientId = clientId)
    }

    override fun create(co: BlogValue.BlogCo): Boolean {
        return insert()
                .set("client_id", co.clientId)
                .set("title", co.title)
                .set("head_img", co.headImg)
                .set("sort", co.sort)
                .set("platform", co.platform)
                .executeOnlyOne()
    }

    override fun update(uo: BlogValue.BlogUo): Boolean {
        return update()
                .set("title", uo.title)
                .set("head_img", uo.headImg)
                .set("sort", uo.sort)
                .set("platform", uo.platform)
                .set("status", uo.status)
                .where("id", uo.id)
                .executeOnlyOne()

    }
}