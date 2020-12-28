package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.ApplyState
import com.onepiece.gpgaming.beans.enums.CommissionType
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Commission
import com.onepiece.gpgaming.beans.value.database.AgentApplyValue
import com.onepiece.gpgaming.beans.value.database.AgentReportValue
import com.onepiece.gpgaming.beans.value.database.AgentValue
import com.onepiece.gpgaming.beans.value.database.CommissionValue
import com.onepiece.gpgaming.beans.value.database.MemberCo
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.beans.value.database.MemberReportValue
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.beans.value.internet.web.MemberValue
import com.onepiece.gpgaming.core.dao.AgentMonthReportDao
import com.onepiece.gpgaming.core.dao.AnalysisDao
import com.onepiece.gpgaming.core.dao.MemberDailyReportDao
import com.onepiece.gpgaming.core.service.AgentApplyService
import com.onepiece.gpgaming.core.service.CommissionService
import com.onepiece.gpgaming.core.service.LevelService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/agent")
class AgentApiController(
        private val commissionService: CommissionService,
        private val memberService: MemberService,
        private val levelService: LevelService,
        private val agentMonthReportDao: AgentMonthReportDao,
//        private val agentDailyReportDao: AgentMonthReportDao,
        private val memberDailyReportDao: MemberDailyReportDao,
        private val analysisDao: AnalysisDao,
        private val agentApplyService: AgentApplyService
) : BasicController(), AgentApi {

    @GetMapping("/commission/setting")
    override fun commission(@RequestParam("type") type: CommissionType): List<Commission> {
        val bossId = getBossId()
        return commissionService.list(bossId = bossId, type = type)
    }

    @PostMapping("/commission/setting")
    override fun commissionCreate(@RequestBody co: CommissionValue.CommissionCo) {
        val bossId = getBossId()
        commissionService.create(co = co.copy(bossId = bossId))
    }

    @PutMapping("/commission/setting")
    override fun commissionUpdate(@RequestBody uo: CommissionValue.CommissionUo) {
        commissionService.update(uo = uo)
    }

    @GetMapping("/apply")
    override fun applies(): List<MemberValue.Agent> {

        val current = this.current()

        // 查询代理申请
        val applyQuery = AgentApplyValue.ApplyQuery(bossId = current.bossId, clientId = current.clientId, state = ApplyState.Process)
        val applies = agentApplyService.list(applyQuery)
        val agentIds = applies.map { it.agentId }
        if (applies.isEmpty()) return emptyList()


        // 查询用户基本信息
        val memberQuery = MemberQuery(bossId = current.bossId, clientId = current.clientId, agentId = null, username = null, ids = agentIds, role = Role.Agent,
                name = null, phone = null, levelId = null, startTime = null, endTime = null, status = null, promoteCode = null)
        val data = memberService.list(memberQuery)


        // 查询上级代理信息
        val superiorIds = data.map { it.agentId }.filter { it == -1 }
        val agentQuery = MemberQuery(ids = superiorIds)
        val superiorAgents = memberService.list(memberQuery = agentQuery)
                .map { it.id to it }
                .toMap()


        return data.map {

            val superiorAgent = superiorAgents[it.agentId]
            val superiorAgentId = superiorAgent?.id ?: -1
            val superiorUsername = superiorAgent?.username ?: "-"


            val apply = applies.first { a -> a.agentId == it.id }

            MemberValue.Agent(id = apply.id, agentId = it.agentId, username = it.username, name = it.name, phone = it.phone, status = it.status, createdTime = it.createdTime,
                    loginTime = it.loginTime, loginIp = it.loginIp, promoteCode = it.promoteCode, superiorAgentId = superiorAgentId, superiorUsername = superiorUsername)
        }
    }

    @PutMapping("/apply/check")
    override fun check(@RequestBody checkReq: AgentValue.AgentCheckReq) {
        agentApplyService.check(id = checkReq.id, state = checkReq.state, remark = checkReq.remark?: "", agencyMonthFee = checkReq.agencyMonthFee)
    }


    @PostMapping
    override fun create(@RequestBody req: AgentValue.AgentCoByAdmin) {

        val user = this.current()

        val defaultLevel = levelService.getDefaultLevel(clientId = user.clientId)

        //val superiorAgentId = req.superiorAgentId ?: -1
        val superiorAgentId = -1
        val memberCo = MemberCo(bossId = user.bossId, clientId = user.bossId, agentId = superiorAgentId, levelId = defaultLevel.id, name = req.name, phone = req.phone,
                username = req.username, password = req.password, promoteCode = null, role = Role.Agent, safetyPassword = req.password, formal = true, saleId = -1,
                registerIp = "admin:register", birthday = req.birthday, email = req.email)
        val id = memberService.create(memberCo)


//        val member = memberService.findByUsername(clientId = user.clientId, username = req.username) ?: error("注册失败")
        val applyCo = AgentApplyValue.ApplyCo(bossId = user.bossId, clientId = user.clientId, agentId = id, state = ApplyState.Done, remark = "client add agent")
        agentApplyService.create(applyCo)
    }

    @GetMapping
    override fun agents(
            @RequestParam("superiorAgentId", required = false) superiorAgentId: Int?,
            @RequestParam("username", required = false) username: String?
    ): List<AgentValue.SubAgentVo> {
        val current = this.current()
        val agentId = when {
            superiorAgentId != null -> superiorAgentId
            username != null -> {
                memberService.findByUsername(clientId = getClientId(), username = username)?.id?: -1
            }
            else -> -1
        }

        val list = analysisDao.subAgents(bossId = current.bossId, clientId = current.clientId, agentId = agentId)
        if (list.isEmpty()) return emptyList()


        val superiorIds = list.map { it.superiorAgentId }.filter { it != -1 }
        val memberQuery = MemberQuery(ids = superiorIds)
        val agents = memberService.query(memberQuery = memberQuery, current = 0, size = 999999)
                .data
                .map { it.id to it }
                .toMap()

        return list.map {
            val superior = agents[it.superiorAgentId]?.username?: "-"
            it.copy(superiorUsername = superior)
        }
    }

    @PutMapping
    override fun update(@RequestBody req: MemberValue.AgentUo) {

        val agent = memberService.getMember(req.id)
        check(agent.clientId == this.getClientId()) { OnePieceExceptionCode.DATA_FAIL }

        val memberUo = MemberUo(id = req.id, agencyMonthFee = req.agencyMonthFee, name = req.name, password = req.password, status = req.status)
        memberService.update(memberUo)
    }

    @GetMapping("/commission")
    override fun commissions(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate
    ): List<AgentValue.AgentCommissionVo> {

        val current = this.current()

        val reportQuery = AgentReportValue.AgentMonthQuery(bossId = current.bossId, clientId = current.clientId,  agentId = null, startDate = startDate,
                endDate = endDate)
        return agentMonthReportDao.query(reportQuery).map {
            AgentValue.AgentCommissionVo(day = it.day, totalDeposit = it.totalDeposit, totalWithdraw = it.totalWithdraw, totalBet = it.totalBet,
                    payout = it.payout,  totalRebate = it.totalRebate, totalPromotion = it.totalPromotion, newMemberCount = it.newMemberCount,
                    subAgentCommission = it.agentCommission, memberCommission = it.memberCommission, agentId = it.agentId, username = it.username)
        }
    }


    @GetMapping("/commission/sub")
    override fun subCommissions(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate,
            @RequestParam("agentId") agentId: Int
    ): List<AgentValue.AgentCommissionVo> {
        val current = this.current()
        val reportQuery = AgentReportValue.AgentMonthQuery(bossId = current.bossId, clientId = current.clientId,  superiorAgentId = current.id)
        return agentMonthReportDao.query(reportQuery).map {
            AgentValue.AgentCommissionVo(day = it.day, totalDeposit = it.totalDeposit, totalWithdraw = it.totalWithdraw, totalBet = it.totalBet,
                    payout = it.payout,  totalRebate = it.totalRebate, totalPromotion = it.totalPromotion, newMemberCount = it.newMemberCount,
                    subAgentCommission = it.agentCommission, memberCommission = it.memberCommission, agentId = it.agentId, username = it.username)
        }
    }

    @GetMapping("/commission/member")
    override fun memberCommissions(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate,
            @RequestParam("agentId") agentId: Int
    ): List<AgentValue.MemberCommissionVo> {

        val current = this.current()

        val collectQuery = MemberReportValue.CollectQuery(bossId = current.bossId, clientId = current.clientId, startDate = startDate, endDate = endDate,
                agentId = current.id)

        return memberDailyReportDao.collect(query = collectQuery).map {
            val username = it.username
            val size = username.length

            val first = username.substring(0, 1)
            val last = username.substring(size - 1, size)

            val newUsername = "${first}****${last}"

            AgentValue.MemberCommissionVo(username = newUsername, totalBet = it.totalBet, payout = it.payout, totalRebate = it.rebateAmount, totalPromotion = it.promotionAmount)
        }
    }

}