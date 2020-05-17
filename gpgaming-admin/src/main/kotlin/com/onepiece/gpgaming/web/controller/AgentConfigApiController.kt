package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.CommissionType
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Commission
import com.onepiece.gpgaming.beans.value.database.CommissionValue
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.beans.value.internet.web.MemberValue
import com.onepiece.gpgaming.core.service.CommissionService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/agent")
class AgentConfigApiController(
        private val commissionService: CommissionService,
        private val memberService: MemberService
) : BasicController(), AgentConfigApi {

    @GetMapping("/commission")
    override fun commission(@RequestParam("type") type: CommissionType): List<Commission> {
        val bossId = getBossId()
        return commissionService.list(bossId = bossId, type = type)
    }

    @PostMapping("/commission")
    override fun commissionCreate(@RequestBody co: CommissionValue.CommissionCo) {
        val bossId = getBossId()
        commissionService.create(co = co.copy(bossId = bossId))
    }

    @PutMapping("/commission")
    override fun commissionUpdate(@RequestBody uo: CommissionValue.CommissionUo) {
        commissionService.update(uo = uo)
    }

    @GetMapping
    override fun agents(
            @RequestParam("username") username: String,
            @RequestParam("superiorUsername") superiorUsername: String
    ): List<MemberValue.Agent> {

        val bossId = getBossId()

        val agentId = if (superiorUsername.isNullOrBlank()) {
            memberService.findByUsername(clientId = getClientId(), username = superiorUsername)?.id
        } else null

        //TODO 显示代理下有多少会员等

        val query = MemberQuery(bossId = bossId, username = username, role = Role.Agent, agentId = agentId, clientId = null, status = Status.Normal,
                startTime = null, endTime = null, levelId = null, name = null, phone = null, promoteCode = null)
        return memberService.query(query,0, 1000).data.map {
            MemberValue.Agent(id = it.id, agentId = it.agentId, username = it.username, name = it.username, phone = it.phone, status = it.status, createdTime = it.createdTime,
                    loginIp = it.loginIp, promoteCode = it.promoteCode, loginTime = it.loginTime)
        }

    }
}