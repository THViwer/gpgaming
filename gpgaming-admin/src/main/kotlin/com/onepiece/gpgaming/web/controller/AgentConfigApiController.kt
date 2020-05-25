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
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.beans.value.database.MemberReportValue
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.beans.value.internet.web.MemberValue
import com.onepiece.gpgaming.core.dao.AgentApplyDao
import com.onepiece.gpgaming.core.dao.AgentMonthReportDao
import com.onepiece.gpgaming.core.dao.AnalysisDao
import com.onepiece.gpgaming.core.dao.MemberDailyReportDao
import com.onepiece.gpgaming.core.service.AgentApplyService
import com.onepiece.gpgaming.core.service.CommissionService
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
import java.math.BigDecimal
import java.time.LocalDate

@RestController
@RequestMapping("/agent")
class AgentConfigApiController(
        private val commissionService: CommissionService,
        private val memberService: MemberService,
        private val agentMonthReportDao: AgentMonthReportDao,
        private val agentDailyReportDao: AgentMonthReportDao,
        private val memberDailyReportDao: MemberDailyReportDao,
        private val analysisDao: AnalysisDao,
        private val agentApplyService: AgentApplyService
) : BasicController(), AgentConfigApi {

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

        val applyQuery = AgentApplyValue.ApplyQuery(bossId = current.bossId, clientId = current.clientId, state = ApplyState.Process)
        val applies = agentApplyService.list(applyQuery)
                .map { it.agentId }

        val memberQuery = MemberQuery(bossId = current.bossId, clientId = current.clientId, agentId = null, username = null, ids = applies, role = Role.Agent,
                name = null, phone = null, levelId = null, startTime = null, endTime = null, status = null, promoteCode = null)
        val data = memberService.query(memberQuery, 0, 999999).data

        return data.map {
            MemberValue.Agent(id = it.id, agentId = it.agentId, username = it.username, name = it.name, phone = it.phone, status = it.status, createdTime = it.createdTime,
                    loginTime = it.loginTime, loginIp = it.loginIp, promoteCode = it.promoteCode)
        }
    }

    @PutMapping("/apply/check")
    override fun check(
            @RequestParam("id") id: Int,
            @RequestParam("state") state: ApplyState,
            @RequestParam("remark") remark: String,
            @RequestParam("agencyMonthFee") agencyMonthFee: BigDecimal
    ) {
        agentApplyService.check(id = id, state = state, remark = remark, agencyMonthFee = agencyMonthFee)
    }

    @GetMapping
    override fun agents(
            @RequestParam("username", required = false) username: String?
    ): List<AgentValue.SubAgentVo> {
        val current = this.current()
        val agentId = memberService.findByUsername(clientId = getClientId(), username = username)?.id?: -1
        return analysisDao.subAgents(bossId = current.bossId, clientId = current.clientId, agentId = agentId)
    }

    @PutMapping
    override fun update(@RequestBody req: MemberValue.AgentUo) {

        val agent = memberService.getMember(req.id)
        check(agent.clientId == this.getClientId()) { OnePieceExceptionCode.DATA_FAIL }

        val memberUo = MemberUo(id = req.id, agencyMonthFee = req.agencyMonthFee)
        memberService.update(memberUo)
    }

    @GetMapping("/commission")
    override fun commissions(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate
    ): List<AgentValue.AgentCommissionVo> {

        val current = this.current()

        val reportQuery = AgentReportValue.AgentMonthQuery(bossId = current.bossId, clientId = current.clientId,  agentId = current.id)
        return agentMonthReportDao.query(reportQuery).map {
            AgentValue.AgentCommissionVo(day = it.day, totalDeposit = it.totalDeposit, totalWithdraw = it.totalWithdraw, totalBet = it.totalBet,
                    totalMWin = it.totalMWin,  totalRebate = it.totalRebate, totalPromotion = it.totalPromotion, newMemberCount = it.newMemberCount,
                    subAgentCommission = it.agentCommission, memberCommission = it.memberCommission)
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
                    totalMWin = it.totalMWin,  totalRebate = it.totalRebate, totalPromotion = it.totalPromotion, newMemberCount = it.newMemberCount,
                    subAgentCommission = it.agentCommission, memberCommission = it.memberCommission)
        }
    }

    @GetMapping("commission/member")
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

            AgentValue.MemberCommissionVo(username = newUsername, totalBet = it.totalBet, totalMWin = it.totalMWin, totalRebate = it.rebateAmount, totalPromotion = it.promotionAmount)
        }
    }

}