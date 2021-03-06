package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.enums.ApplyState
import com.onepiece.gpgaming.beans.model.AgentApply
import com.onepiece.gpgaming.beans.value.database.AgentApplyValue
import java.math.BigDecimal

interface AgentApplyService {

    fun create(co: AgentApplyValue.ApplyCo)

    fun update(uo: AgentApplyValue.ApplyUo)

    fun check(id: Int, state: ApplyState, remark: String, agencyMonthFee: BigDecimal)

    fun list(query: AgentApplyValue.ApplyQuery): List<AgentApply>


}