package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.WaiterDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.core.dao.value.WaiterCo
import com.onepiece.treasure.core.dao.value.WaiterUo
import com.onepiece.treasure.core.model.Waiter
import com.onepiece.treasure.core.model.enums.Status
import com.onepiece.treasure.utils.JdbcBuilder
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class WaiterDaoImpl : BasicDaoImpl<Waiter>("waiter"), WaiterDao {

    override fun mapper(): (rs: ResultSet) -> Waiter {
        return { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val username = rs.getString("username")
            val password = rs.getString("password")
            val name = rs.getString("name")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val loginTime = rs.getTimestamp("login_time").toLocalDateTime()

            Waiter(id = id, clientId = clientId, username = username, password = password, name = name, status = status,
                    createdTime = createdTime, loginTime = loginTime)
        }
    }

    override fun create(waiterCo: WaiterCo): Boolean {
        return JdbcBuilder.insert(jdbcTemplate, "waiter")
                .set("client_id", waiterCo.clientId)
                .set("username", waiterCo.username)
                .set("password", waiterCo.password)
                .set("name", waiterCo.name)
                .set("status", Status.Normal)
                .executeOnlyOne()
    }

    override fun update(waiterUo: WaiterUo): Boolean {
        return JdbcBuilder.update(jdbcTemplate, "waiter")
                .set("password", waiterUo.password)
                .set("name", waiterUo.name)
                .set("status", waiterUo.status)
                .where("id", waiterUo.id)
                .executeOnlyOne()
    }
}