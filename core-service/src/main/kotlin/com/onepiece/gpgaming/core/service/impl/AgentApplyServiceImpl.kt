package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.ApplyState
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.AgentApply
import com.onepiece.gpgaming.beans.value.database.AgentApplyValue
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.beans.value.internet.web.CashValue
import com.onepiece.gpgaming.core.dao.AgentApplyDao
import com.onepiece.gpgaming.core.service.AgentApplyService
import com.onepiece.gpgaming.core.service.MemberService
import org.springframework.stereotype.Service

@Service
class AgentApplyServiceImpl(
        private val agentApplyDao: AgentApplyDao,
        private val memberService: MemberService
) : AgentApplyService {

    override fun create(co: AgentApplyValue.ApplyCo) {
        val flag = agentApplyDao.create(co)
        check(flag) { OnePieceExceptionCode.DATA_FAIL }
    }

    override fun update(uo: AgentApplyValue.ApplyUo) {
        val flag = agentApplyDao.update(uo)
        check(flag) { OnePieceExceptionCode.DATA_FAIL }
    }

    override fun check(id: Int, state: ApplyState, remark: String) {

        val apply = agentApplyDao.get(id = id)
        check(apply.state == ApplyState.Process) { OnePieceExceptionCode.DATA_FAIL }


        // 更新状态
        val uo = AgentApplyValue.ApplyUo(id = apply.id, state = state, remark = remark)
        agentApplyDao.update(uo)

        // 更新用户
        if (state == ApplyState.Done) {
            val memberUo = MemberUo(id = apply.agentId, formal = true)
            memberService.update(memberUo)
        }

    }

    override fun list(query: AgentApplyValue.ApplyQuery): List<AgentApply> {
        return agentApplyDao.list(query)
    }
}