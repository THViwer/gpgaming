package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.AgentApply
import com.onepiece.gpgaming.beans.value.database.AgentApplyValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface AgentApplyDao : BasicDao<AgentApply> {

    fun create(co: AgentApplyValue.ApplyCo): Boolean

    fun update(uo: AgentApplyValue.ApplyUo): Boolean

    fun list(query: AgentApplyValue.ApplyQuery): List<AgentApply>

}