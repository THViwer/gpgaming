package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.Waiter
import com.onepiece.treasure.beans.value.database.WaiterCo
import com.onepiece.treasure.beans.value.database.WaiterUo
import com.onepiece.treasure.core.dao.WaiterDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class WaiterDaoImpl : BasicDaoImpl<Waiter>("waiter"), WaiterDao {

    override val mapper: (rs: ResultSet) -> Waiter
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val username = rs.getString("username")
            val password = rs.getString("password")
            val name = rs.getString("name")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val loginIp = rs.getString("login_ip")
            val loginTime = rs.getTimestamp("login_time")?.toLocalDateTime()

            Waiter(id = id, clientId = clientId, username = username, password = password, name = name, status = status,
                    createdTime = createdTime, loginIp = loginIp, loginTime = loginTime)
        }

    override fun findByUsername(username: String): Waiter? {
        return query().where("username", username).executeMaybeOne(mapper)
    }

    override fun create(waiterCo: WaiterCo): Int {
        return insert()
                .set("client_id", waiterCo.clientId)
                .set("username", waiterCo.username)
                .set("password", waiterCo.password)
                .set("name", waiterCo.name)
                .set("status", Status.Normal)
                .executeGeneratedKey()
    }

    override fun update(waiterUo: WaiterUo): Boolean {
        return update()
                .set("password", waiterUo.password)
                .set("name", waiterUo.name)
                .set("status", waiterUo.status)
                .set("login_ip", waiterUo.loginIp)
                .set("login_time", waiterUo.loginTime)
                .where("id", waiterUo.id)
                .executeOnlyOne()
    }
}