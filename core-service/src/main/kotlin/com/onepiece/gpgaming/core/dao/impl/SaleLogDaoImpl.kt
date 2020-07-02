package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.SaleLog
import com.onepiece.gpgaming.beans.value.database.SaleLogValue
import com.onepiece.gpgaming.core.dao.SaleLogDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class SaleLogDaoImpl : BasicDaoImpl<SaleLog>("sale_log"), SaleLogDao {

    override val mapper: (rs: ResultSet) -> SaleLog
        get() = {  rs ->
            val id = rs.getInt("id")
            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val saleId = rs.getInt("sale_id")
            val memberId = rs.getInt("member_id")
            val remark = rs.getString("remark")
            val nextCallTime = rs.getTimestamp("next_call_time")?.toLocalDateTime()
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            SaleLog(id = id, bossId = bossId, clientId = clientId, saleId = saleId, nextCallTime = nextCallTime,
                    memberId = memberId, remark = remark, createdTime = createdTime)
        }

    override fun create(co: SaleLogValue.SaleLogCo): Boolean {
        return insert()
                .set("boss_id", co.bossId)
                .set("client_id", co.clientId)
                .set("sale_id", co.saleId)
                .set("member_id", co.memberId)
                .set("remark", co.remark)
                .executeOnlyOne()
    }

    override fun list(query: SaleLogValue.SaleLogQuery): List<SaleLog> {
        return query()
                .where("boss_id", query.bossId)
                .where("client_id", query.clientId)
                .where("sale_id", query.saleId)
                .where("member_id", query.memberId)
                .execute(mapper)
    }

}