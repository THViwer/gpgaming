package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Waiter
import com.onepiece.gpgaming.beans.value.database.WaiterValue
import com.onepiece.gpgaming.core.dao.WaiterDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class WaiterDaoImpl : BasicDaoImpl<Waiter>("waiter"), WaiterDao {

    override val mapper: (rs: ResultSet) -> Waiter
        get() = { rs ->
            val id = rs.getInt("id")
            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val username = rs.getString("username")
            val password = rs.getString("password")
            val clientBankData = rs.getString("client_bank_data")
            val name = rs.getString("name")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val loginIp = rs.getString("login_ip")
            val loginTime = rs.getTimestamp("login_time")?.toLocalDateTime()
            val ownCustomerScale = rs.getBigDecimal("own_customer_scale")
            val systemCustomerScale = rs.getBigDecimal("system_customer_scale")
            val role = rs.getString("role").let { Role.valueOf(it) }

            Waiter(id = id, clientId = clientId, username = username, password = password, name = name, status = status,
                    createdTime = createdTime, loginIp = loginIp, loginTime = loginTime, clientBankData = clientBankData,
                    bossId = bossId, ownCustomerScale = ownCustomerScale, systemCustomerScale = systemCustomerScale, role = role)
        }

    override fun findByUsername(username: String): Waiter? {
        return query().where("username", username).executeMaybeOne(mapper)
    }

    override fun create(waiterCo: WaiterValue.WaiterCo): Int {
        return insert()
                .set("boss_id", waiterCo.bossId)
                .set("client_id", waiterCo.clientId)
                .set("username", waiterCo.username)
                .set("password", waiterCo.password)
                .set("client_bank_data", waiterCo.clientBankData)
                .set("name", waiterCo.name)
                .set("own_customer_scale", waiterCo.ownCustomerScale)
                .set("system_customer_scale", waiterCo.systemCustomerScale)
                .set("status", Status.Normal)
                .set("role", waiterCo.role)
                .executeGeneratedKey()
    }

    override fun update(waiterUo: WaiterValue.WaiterUo): Boolean {
        return update()
                .set("password", waiterUo.password)
                .set("client_bank_data", waiterUo.clientBankData)
                .set("name", waiterUo.name)
                .set("status", waiterUo.status)
                .set("login_ip", waiterUo.loginIp)
                .set("login_time", waiterUo.loginTime)
                .set("own_customer_scale", waiterUo.ownCustomerScale)
                .set("system_customer_scale", waiterUo.systemCustomerScale)
                .where("id", waiterUo.id)
                .executeOnlyOne()
    }
}