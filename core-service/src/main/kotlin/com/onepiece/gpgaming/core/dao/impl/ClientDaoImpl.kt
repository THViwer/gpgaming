package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Client
import com.onepiece.gpgaming.beans.value.database.ClientCo
import com.onepiece.gpgaming.beans.value.database.ClientUo
import com.onepiece.gpgaming.core.dao.ClientDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ClientDaoImpl : BasicDaoImpl<Client>("client"), ClientDao {

    override val mapper: (rs: ResultSet) -> Client
        get() = { rs ->
            val id = rs.getInt("id")
            val bossId = rs.getInt("boss_id")
            val country = rs.getString("country").let { Country.valueOf(it) }
            val username = rs.getString("username")
            val password = rs.getString("password")
            val logo = rs.getString("logo")
            val shortcutLogo = rs.getString("shortcut_logo")
            val name = rs.getString("name")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val loginIp = rs.getString("login_ip")
            val whitelists = rs.getString("whitelists")?.let { it.split(",") }?: emptyList()
            val loginTime = rs.getTimestamp("login_time")?.toLocalDateTime()
            Client(id = id, username = username, password = password, createdTime = createdTime, loginTime = loginTime,
                    status = status, loginIp = loginIp, name = name, logo = logo, whitelists = whitelists, shortcutLogo = shortcutLogo,
                    bossId = bossId, country = country)
        }

    override fun findByUsername(username: String): Client? {
        return query().where("username", username)
                .executeMaybeOne(mapper)
    }

    override fun create(clientCo: ClientCo): Int {
        return insert()
                .set("boss_id", clientCo.bossId)
                .set("country", clientCo.country)
                .set("logo", clientCo.logo)
                .set("shortcut_logo", clientCo.logo)
                .set("username", clientCo.username)
                .set("password", clientCo.password)
                .set("name", clientCo.name)
                .set("status", Status.Normal)
                .set("whitelists", clientCo.whitelists.joinToString(separator = ","))
                .executeGeneratedKey()
    }

    override fun update(clientUo: ClientUo): Boolean {
        return update().set("password", clientUo.password)
                .set("name", clientUo.name)
                .set("logo", clientUo.logo)
                .set("shortcut_logo", clientUo.shortcutLogo)
                .set("status", clientUo.status)
                .set("login_ip", clientUo.ip)
                .set("login_time", clientUo.loginTime)
                .set("whitelists", clientUo.whitelists?.joinToString(separator = ","))
                .where("id", clientUo.id)
                .executeOnlyOne()
    }

//    override fun updateEarnestBalance(id: Int, earnestBalance: BigDecimal, processId: String): Boolean {
//        return update()
//                .asSet("earnest_balance = earnest_balance + $earnestBalance")
//                .set("process_id", UUID.randomUUID().toString())
//                .where("id", id)
//                .where("process_id", processId)
//                .asWhere("earnest_balance >= ?", earnestBalance)
//                .executeOnlyOne()
//    }
}