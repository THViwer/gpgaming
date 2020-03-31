package com.onepiece.gpgaming.core.dao.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.enums.PayType
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.PayBind
import com.onepiece.gpgaming.beans.value.database.PayBindValue
import com.onepiece.gpgaming.core.dao.PayBindDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import com.onepiece.gpgaming.core.dao.basic.getIntOrNull
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PayBindDaoImpl(
        private val objectMapper: ObjectMapper
) : BasicDaoImpl<PayBind>("pay_bind"), PayBindDao {

    override val mapper: (rs: ResultSet) -> PayBind
        get() = { rs ->

            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val levelId = rs.getIntOrNull("level_id")
            val payType = rs.getString("pay_type").let { PayType.valueOf(it) }
            val configJson = rs.getString("config_json")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            PayBind(id = id, clientId = clientId, levelId = levelId, payType = payType, configJson = configJson, status = status,
                    createdTime = createdTime)
        }

    override fun create(co: PayBindValue.PayBindCo): Boolean {
        return insert()
                .set("client_id", co.clientId)
                .set("level_id", co.levelId)
                .set("pay_type", co.payType)
                .set("config_json", co.configJson)
                .set("status", co.status)
                .executeOnlyOne()
    }

    override fun update(uo: PayBindValue.PayBindUo): Boolean {
        return update()
                .set("level_id", uo.levelId)
                .set("config_json", uo.configJson)
                .set("status", uo.status)
                .where("id", uo.id)
                .executeOnlyOne()
    }
}