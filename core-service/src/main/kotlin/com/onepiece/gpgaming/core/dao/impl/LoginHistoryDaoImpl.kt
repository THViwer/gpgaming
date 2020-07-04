package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.model.LoginHistory
import com.onepiece.gpgaming.beans.value.database.LoginHistoryValue
import com.onepiece.gpgaming.core.dao.LoginHistoryDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class LoginHistoryDaoImpl : BasicDaoImpl<LoginHistory>("login_history"), LoginHistoryDao {

    override val mapper: (rs: ResultSet) -> LoginHistory
        get() = {  rs ->

            val id = rs.getInt("id")
            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val userId = rs.getInt("user_id")
            val username = rs.getString("username")
            val role = rs.getString("role").let { Role.valueOf(it) }
            val ip = rs.getString("ip")
            val country = rs.getString("country")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            LoginHistory(id = id, bossId = bossId, clientId = clientId, userId = userId, role = role, ip = ip,
                    country = country, createdTime = createdTime, username = username)
        }

    override fun create(co: LoginHistoryValue.LoginHistoryCo): Boolean {
        return insert()
                .set("boss_id", co.bossId)
                .set("client_id", co.clientId)
                .set("user_id", co.userId)
                .set("username", co.username)
                .set("role", co.role)
                .set("ip", co.ip)
                .set("country", co.country)
                .executeOnlyOne()
    }

    override fun list(userId: Int, role: Role): List<LoginHistory> {

        return query()
                .where("user_id", userId)
                .where("role", role)
                .sort("order by id desc")
                .limit(0, 100)
                .execute(mapper)
    }
}