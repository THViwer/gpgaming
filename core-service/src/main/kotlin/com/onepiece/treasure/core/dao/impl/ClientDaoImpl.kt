package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.ClientDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.core.dao.value.ClientCo
import com.onepiece.treasure.core.dao.value.ClientUo
import com.onepiece.treasure.core.model.Client
import com.onepiece.treasure.core.model.enums.Status
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ClientDaoImpl : BasicDaoImpl<Client>("client"), ClientDao {

    override fun mapper(): (rs: ResultSet) -> Client {
        return { rs ->
            val id = rs.getInt("id")
            val brand = rs.getString("brand")
            val username = rs.getString("username")
            val password = rs.getString("password")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val loginTime = rs.getTimestamp("login_time").toLocalDateTime()
            val status = rs.getString("status").let { Status.valueOf(it) }
            Client(id = id, brand = brand, username = username, password = password, createdTime = createdTime, loginTime = loginTime,
                    status = status)
        }
    }

    override fun create(clientCo: ClientCo): Boolean {
        return insert().set("brand", clientCo.brand)
                .set("username", clientCo.username)
                .set("password", clientCo.password)
                .set("status", Status.Normal)
                .executeOnlyOne()
    }

    override fun update(clientUo: ClientUo): Boolean {
        return update().set("password", clientUo.password)
                .set("status", clientUo.status)
                .where("id", clientUo.id)
                .executeOnlyOne()

    }
}