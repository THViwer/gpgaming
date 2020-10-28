package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.model.ComingSoon
import com.onepiece.gpgaming.core.dao.ComingSoonDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ComingSoonDaoImpl : BasicDaoImpl<ComingSoon>("coming_soon"), ComingSoonDao {

    override val mapper: (rs: ResultSet) -> ComingSoon
        get() = { rs ->
            val id = rs.getInt("id")
            val ip = rs.getString("ip")
            val email = rs.getString("email")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            ComingSoon(id = id, ip = ip, email = email, createdTime = createdTime)
        }

    override fun create(ip: String, email: String, launch: LaunchMethod): Boolean {
        return this.insert()
                .set("ip", ip)
                .set("email", email)
                .set("launch", launch)
                .executeOnlyOne()
    }
}