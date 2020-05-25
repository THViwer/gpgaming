package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.beans.enums.ApplyState
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.value.database.AgentApplyValue
import com.onepiece.gpgaming.beans.value.database.AgentReportValue
import com.onepiece.gpgaming.beans.value.database.AgentValue
import com.onepiece.gpgaming.beans.value.database.LoginValue
import com.onepiece.gpgaming.beans.value.database.MemberCo
import com.onepiece.gpgaming.beans.value.database.MemberReportValue
import com.onepiece.gpgaming.core.dao.AgentMonthReportDao
import com.onepiece.gpgaming.core.dao.AnalysisDao
import com.onepiece.gpgaming.core.dao.MemberDailyReportDao
import com.onepiece.gpgaming.core.service.AgentApplyService
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.core.service.LevelService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.ReportService
import com.onepiece.gpgaming.core.service.WalletService
import com.onepiece.gpgaming.player.controller.basic.BasicController
import com.onepiece.gpgaming.player.controller.value.LoginReq
import com.onepiece.gpgaming.player.jwt.AuthService
import com.onepiece.gpgaming.utils.RequestUtil
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@RestController
@RequestMapping("/agent")
class AgentApiController(
        private val memberService: MemberService,
        private val clientService: ClientService,
        private val levelService: LevelService,
        private val authService: AuthService,
        private val walletService: WalletService,
        private val analysisDao: AnalysisDao,
        private val agentApplyService: AgentApplyService,
        private val agentMonthReportDao: AgentMonthReportDao,
        private val memberDailyReportDao: MemberDailyReportDao,
        private val reportService: ReportService
) : BasicController(), AgentApi {


    @PutMapping
    override fun register(@RequestBody req: AgentValue.AgentRegisterReq) {


        val bossId = getBossIdByDomain()
        val mainClient = clientService.getMainClient(bossId) ?: error("没有默认代理")


        val superiorAgent = req.code?.let { memberService.getAgentByCode(bossId = bossId, clientId = mainClient.id, code = it) }
        val agentId = superiorAgent?.id?: -1

        val level = levelService.getDefaultLevel(mainClient.id)

        // 创建代理
        val memberCo = MemberCo(bossId = bossId, clientId = mainClient.id, agentId = agentId, levelId = level.id, name = req.name, phone = req.phone,
                username = req.username, password = req.password, promoteCode = null, role = Role.Agent, safetyPassword = req.password, formal = false)
        memberService.create(memberCo)


        // 代理申请表
        val member = memberService.findByUsername(clientId = mainClient.id, username = req.username) ?: error("注册失败")
        val applyCo = AgentApplyValue.ApplyCo(bossId = member.bossId, clientId = member.clientId, agentId = member.id, state = ApplyState.Process)
        agentApplyService.create(applyCo)
    }

    @PostMapping
    override fun login(@RequestBody loginReq: LoginReq): AgentValue.AgentLoginResp {

        val bossId = getBossIdByDomain()

        val loginValue = LoginValue(bossId = bossId, username = loginReq.username, password = loginReq.password, ip = RequestUtil.getIpAddress())
        val member = memberService.login(loginValue)
        check(member.role == Role.Agent) { OnePieceExceptionCode.LOGIN_FAIL }
        check(member.formal) { OnePieceExceptionCode.AGENT_PROCESS }

        val token = authService.login(bossId = bossId, clientId = member.clientId, username = loginReq.username, role = member.role)

        val sites = webSiteService.getDataByBossId(bossId = bossId)

        val urls = sites.groupBy { it.country }.map { it.value.first() }.map {

            val promoteURL = "https://www.${it.domain}"
            val mobilePromoteURL = "https://www.${it.domain}/m"

            AgentValue.PromoteVo(country = it.country, promoteURL = promoteURL, mobilePromoteURL = mobilePromoteURL)
        }

        return AgentValue.AgentLoginResp(token = token, name = member.name, urls = urls, promoteCode = member.promoteCode)
    }

    @GetMapping("/info")
    override fun info(): AgentValue.AgentInfo {

        val memberId = this.current().id

        val agent = memberService.getMember(memberId)
        val wallet = walletService.getMemberWallet(memberId = memberId)

        val agentCount = analysisDao.memberCount(agentId = memberId, role = Role.Agent)
        val memberCount = analysisDao.memberCount(agentId = memberId, role = Role.Member)

        // 当前这个月佣金
        val startDate  = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
        val agentMonthReport = reportService.startAgentMonthReport(agentId = memberId, today = startDate)
                .first()


        return AgentValue.AgentInfo(balance = wallet.balance, subAgentCount = agentCount, memberCount = memberCount,
        subAgentCommission = agentMonthReport.agentCommission, memberCommission = agentMonthReport.memberCommission, agencyMonthFee = agent.agencyMonthFee)
    }

    @GetMapping("/sub")
    override fun subAgents(): List<AgentValue.SubAgentVo> {

        val current = this.current()
        return analysisDao.subAgents(bossId = current.bossId, clientId = current.clientId, agentId =  current.id)
    }

    @GetMapping("/commission")
    override fun commissions(
            @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") endDate:  LocalDate
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
            @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") endDate:  LocalDate,
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
            @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") endDate:  LocalDate,
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