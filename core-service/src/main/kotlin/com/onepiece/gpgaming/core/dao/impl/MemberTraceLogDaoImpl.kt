package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.TraceType
import com.onepiece.gpgaming.beans.model.MemberTraceLog
import com.onepiece.gpgaming.beans.value.database.MemberTraceLogValue
import com.onepiece.gpgaming.core.dao.MemberTraceLogDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class MemberTraceLogDaoImpl: BasicDaoImpl<MemberTraceLog>("member_trace_log"), MemberTraceLogDao {

    override val mapper: (rs: ResultSet) -> MemberTraceLog
        get() = { rs ->
            val id = rs.getInt("id")
            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val saleId = rs.getInt("sale_id")
            val memberId = rs.getInt("member_id")
            val type = rs.getString("trace_type").let { TraceType.valueOf(it) }
            val remark = rs.getString("remark")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            MemberTraceLog(id = id, bossId = bossId, clientId = clientId, saleId = saleId, memberId = memberId,
                    type = type, remark = remark, createdTime = createdTime)
        }

    override fun create(co: MemberTraceLogValue.MemberTraceLogCo): Boolean {
        return insert()
                .set("boss_id", co.bossId)
                .set("client_id", co.clientId)
                .set("sale_id", co.saleId)
                .set("member_id", co.memberId)
                .set("trace_type", co.type)
                .set("remark", co.remark)
                .executeOnlyOne()
    }

    override fun list(query: MemberTraceLogValue.MemberTraceLogQuery): List<MemberTraceLog> {
        return query()
                .where("boss_id", query.bossId)
                .where("client_id", query.clientId)
                .where("sale_id", query.saleId)
                .where("member_id", query.memberId)
                .execute(mapper)
    }
}