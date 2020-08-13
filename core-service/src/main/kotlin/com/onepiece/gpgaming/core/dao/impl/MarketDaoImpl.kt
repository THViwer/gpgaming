package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Market
import com.onepiece.gpgaming.beans.value.database.MarketingValue
import com.onepiece.gpgaming.core.dao.MarketDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class MarketDaoImpl : MarketDao, BasicDaoImpl<Market>("market") {

    override val mapper: (rs: ResultSet) -> Market
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val name =  rs.getString("name")
            val promotionId = rs.getInt("promotion_id")
            val promotionCode = rs.getString("promotion_code")
            val messageTemplate = rs.getString("message_template")
            val status = rs.getString("status").let { Status.valueOf(it) }

            Market(id = id, clientId = clientId, promotionId = promotionId, promotionCode = promotionCode,
                    messageTemplate = messageTemplate, name = name, status = status)
        }

    override fun create(co: MarketingValue.MarketingCo): Boolean {
        return insert()
                .set("client_id", co.clientId)
                .set("name", co.name)
                .set("promotion_id", co.promotionId)
                .set("promotion_code", co.promotionCode)
                .set("message_template",  co.messageTemplate)
                .executeOnlyOne()
    }

    override fun update(uo: MarketingValue.MarketingUo): Boolean {
        return  update()
                .set("name", uo.name)
                .set("promotion_id", uo.promotionId)
                .set("promotion_code", uo.promotionCode)
                .set("message_template", uo.messageTemplate)
                .where("id", uo.id)
                .executeOnlyOne()
    }

}