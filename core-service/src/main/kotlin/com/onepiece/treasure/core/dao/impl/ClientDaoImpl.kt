package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.Client
import com.onepiece.treasure.beans.value.database.ClientCo
import com.onepiece.treasure.beans.value.database.ClientUo
import com.onepiece.treasure.core.dao.ClientDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet
import java.util.*

@Repository
class ClientDaoImpl : BasicDaoImpl<Client>("client"), ClientDao {

    override val mapper: (rs: ResultSet) -> Client
        get() = { rs ->
            val id = rs.getInt("id")
            val brand = rs.getString("brand")
            val username = rs.getString("username")
            val password = rs.getString("password")
            val name = rs.getString("name")
            val processId = rs.getString("process_id")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val loginIp = rs.getString("login_ip")
            val loginTime = rs.getTimestamp("login_time")?.toLocalDateTime()
            Client(id = id, brand = brand, username = username, password = password, createdTime = createdTime, loginTime = loginTime,
                    status = status, loginIp = loginIp, name = name, processId = processId)
        }

    override fun findByUsername(username: String): Client? {
        return query().where("username", username)
                .executeMaybeOne(mapper)
    }

    override fun create(clientCo: ClientCo): Int {
        return insert()
                .set("username", clientCo.username)
                .set("password", clientCo.password)
                .set("name", clientCo.name)
                .set("processId", UUID.randomUUID().toString())
                .set("status", Status.Normal)
                .executeGeneratedKey()
    }

    override fun update(clientUo: ClientUo): Boolean {
        return update().set("password", clientUo.password)
                .set("name", clientUo.name)
                .set("status", clientUo.status)
                .set("login_ip", clientUo.ip)
                .set("login_time", clientUo.loginTime)
                .where("id", clientUo.id)
                .executeOnlyOne()
    }

    override fun updateEarnestBalance(id: Int, earnestBalance: BigDecimal, processId: String): Boolean {
        return update()
                .asSet("earnest_balance = earnest_balance + $earnestBalance")
                .set("process_id", UUID.randomUUID().toString())
                .where("id", id)
                .where("process_id", processId)
                .asWhere("earnest_balance >= ?", earnestBalance)
                .executeOnlyOne()
    }
}