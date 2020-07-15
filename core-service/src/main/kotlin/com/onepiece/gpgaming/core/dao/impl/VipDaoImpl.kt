package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Vip
import com.onepiece.gpgaming.beans.value.database.VipValue
import com.onepiece.gpgaming.core.dao.VipDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class VipDaoImpl : BasicDaoImpl<Vip>("vip"), VipDao {

    override val mapper: (rs: ResultSet) -> Vip
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val name = rs.getString("name")
            val levelId = rs.getInt("level_id")
            val logo = rs.getString("logo")
            val days = rs.getString("days")
            val depositAmount = rs.getBigDecimal("deposit_amount")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            Vip(id = id, clientId = clientId, name = name, levelId = levelId, logo = logo, days = days,
                    depositAmount = depositAmount, status = status, createdTime = createdTime)
        }

    override fun create(co: VipValue.VipCo): Boolean {
        return insert()
                .set("client_id", co.clientId)
                .set("name", co.name)
                .set("level_id", co.levelId)
                .set("logo", co.logo)
                .set("days", co.days)
                .set("deposit_amount", co.depositAmount)
                .executeOnlyOne()
    }

    override fun update(uo: VipValue.VipUo): Boolean {
        return update()
                .set("name", uo.name)
                .set("level_id", uo.levelId)
                .set("logo", uo.logo)
                .set("days", uo.days)
                .set("deposit_amount", uo.depositAmount)
                .set("status", uo.status)
                .executeOnlyOne()
    }
}