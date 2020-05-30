package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.ApplyState
import com.onepiece.gpgaming.beans.model.AgentApply
import com.onepiece.gpgaming.beans.value.database.AgentApplyValue
import com.onepiece.gpgaming.core.dao.AgentApplyDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class AgentApplyDaoImpl : BasicDaoImpl<AgentApply>("agent_apply"), AgentApplyDao {

    override val mapper: (rs: ResultSet) -> AgentApply
        get() = { rs ->

            val id = rs.getInt("id")
            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val agentId = rs.getInt("agent_id")
            val state = rs.getString("state").let { ApplyState.valueOf(it) }
            val remark = rs.getString("remark")
            val checkTime = rs.getTimestamp("check_time")?.toLocalDateTime()
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            AgentApply(id = id, bossId = bossId, clientId = clientId, agentId = agentId, state = state,
                    remark = remark, checkTime = checkTime, createdTime = createdTime)
        }

    override fun create(co: AgentApplyValue.ApplyCo): Boolean {
        return insert()
                .set("agent_id", co.agentId)
                .set("state", co.state)
                .set("remark", co.remark?: "")
                .executeOnlyOne()
    }

    override fun update(uo: AgentApplyValue.ApplyUo): Boolean {
        return update()
                .set("state", uo.state)
                .set("remark", uo.remark)
                .where("id", uo.id)
                .executeOnlyOne()

    }

    override fun list(query: AgentApplyValue.ApplyQuery): List<AgentApply> {
        return query()
                .where("boss_id", query.bossId)
                .where("client_id", query.clientId)
                .where("state", query.state)
                .where("agent_id", query.agentId)
                .execute(mapper)
    }
}