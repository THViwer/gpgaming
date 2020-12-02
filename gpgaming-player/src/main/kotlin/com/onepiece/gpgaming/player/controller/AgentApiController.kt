package com.onepiece.gpgaming.player.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.enums.ApplyState
import com.onepiece.gpgaming.beans.enums.ContactType
import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.AgentMonthReport
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.value.database.AgentApplyValue
import com.onepiece.gpgaming.beans.value.database.AgentReportValue
import com.onepiece.gpgaming.beans.value.database.AgentValue
import com.onepiece.gpgaming.beans.value.database.LoginValue
import com.onepiece.gpgaming.beans.value.database.MemberCo
import com.onepiece.gpgaming.beans.value.database.MemberReportValue
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.core.dao.AgentMonthReportDao
import com.onepiece.gpgaming.core.dao.AnalysisDao
import com.onepiece.gpgaming.core.dao.MemberDailyReportDao
import com.onepiece.gpgaming.core.service.AgentApplyService
import com.onepiece.gpgaming.core.service.ContactService
import com.onepiece.gpgaming.core.service.I18nContentService
import com.onepiece.gpgaming.core.service.LevelService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.ReportService
import com.onepiece.gpgaming.core.service.WalletService
import com.onepiece.gpgaming.player.controller.basic.BasicController
import com.onepiece.gpgaming.player.controller.basic.MathUtil
import com.onepiece.gpgaming.player.controller.value.ChangePwdReq
import com.onepiece.gpgaming.player.controller.value.Contacts
import com.onepiece.gpgaming.player.controller.value.LoginReq
import com.onepiece.gpgaming.player.jwt.AuthService
import com.onepiece.gpgaming.utils.RequestUtil
import org.slf4j.LoggerFactory
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
        private val levelService: LevelService,
        private val authService: AuthService,
        private val walletService: WalletService,
        private val analysisDao: AnalysisDao,
        private val agentApplyService: AgentApplyService,
        private val agentMonthReportDao: AgentMonthReportDao,
        private val memberDailyReportDao: MemberDailyReportDao,
        private val contactService: ContactService,
        private val reportService: ReportService,
        private val objectMapper: ObjectMapper,
        private val i18nContentService: I18nContentService
) : BasicController(), AgentApi {

    private val log = LoggerFactory.getLogger(AgentApiController::class.java)

    @PutMapping
    override fun register(@RequestBody req: AgentValue.AgentRegisterReq) {


        val bossId = getBossId()
        val mainClient = clientService.getMainClient(bossId) ?: error("没有默认代理")


        val superiorAgent = req.code?.let { memberService.getAgentByCode(bossId = bossId, clientId = mainClient.id, code = it) }
        val agentId = superiorAgent?.id?: -1

        val level = levelService.getDefaultLevel(mainClient.id)

        // 创建代理
        val memberCo = MemberCo(bossId = bossId, clientId = mainClient.id, agentId = agentId, levelId = level.id, name = req.name, phone = req.phone,
                username = req.username, password = req.password, promoteCode = null, role = Role.Agent, safetyPassword = req.password, formal = false,
                saleId = -1, registerIp = "agent:register", email = null, birthday = null)
        val id = memberService.create(memberCo)


        // 代理申请表
//        val member = memberService.findByUsername(clientId = mainClient.id, username = req.username) ?: error("注册失败")
//        val applyCo = AgentApplyValue.ApplyCo(bossId = member.bossId, clientId = member.clientId, agentId = member.id, state = ApplyState.Process, remark = "")
//        agentApplyService.create(applyCo)

        val applyCo = AgentApplyValue.ApplyCo(bossId = bossId, clientId = mainClient.id, agentId = id, state = ApplyState.Process, remark = "")
        agentApplyService.create(applyCo)
    }

    @PostMapping
    override fun login(@RequestBody loginReq: LoginReq): AgentValue.AgentLoginResp {

        val bossId = getBossId()

        val loginValue = LoginValue(bossId = bossId, username = loginReq.username, password = loginReq.password, ip = RequestUtil.getIpAddress())
        val member = memberService.login(loginValue, deviceType = "pc")
        check(member.role == Role.Agent) { OnePieceExceptionCode.LOGIN_FAIL }
        check(member.formal) { OnePieceExceptionCode.AGENT_PROCESS }

        val token = authService.login(bossId = bossId, clientId = member.clientId, username = loginReq.username, role = member.role)

        return AgentValue.AgentLoginResp(token = token, name = member.name, promoteCode = member.promoteCode)
    }

    @PutMapping("/reset/pwd")
    override fun reset(@RequestBody req: ChangePwdReq) {

        val id = this.current().id

        val memberUo = MemberUo(id = id, oldPassword = req.oldPassword, password = req.password)
        memberService.update(memberUo = memberUo)
    }

    @GetMapping("/index/config")
    override fun config(): AgentValue.AffIndexConfig {

        val mainClient = getMainClient()

        val mainSite = webSiteService.getDataByBossId(bossId = -1).first { it.clientId == mainClient.bossId && it.country == Country.Default }
        val guideUrl = "https://www.${mainSite.domain}"


        val name = mainSite.domain.replace(".com", "")
        return AgentValue.AffIndexConfig(bossId = mainClient.bossId, logo = mainClient.logo, shortcutLogo = mainClient.shortcutLogo, guideUrl = guideUrl, name = name)
    }

    @GetMapping("/info")
    override fun info(): AgentValue.AgentInfo {

        val bossId = this.current().bossId
        val memberId = this.current().id

        // 代理和余额
        val agent = memberService.getMember(memberId)
        val wallet = walletService.getMemberWallet(memberId = memberId)

        // 会员数量
        val agentCount = analysisDao.memberCount(agentId = memberId, role = Role.Agent)
        val memberCount = analysisDao.memberCount(agentId = memberId, role = Role.Member)

        // 当前这个月佣金
        val startDate  = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
        val agentMonthReport = reportService.startAgentMonthReport(agentId = memberId, today = startDate)
                .firstOrNull() ?: AgentMonthReport.empty(bossId = bossId, clientId = agent.clientId, agentId = agent.id, day = startDate)

        // 推广连接码
        val sites = webSiteService.getDataByBossId(bossId = bossId)
        val urls = sites.groupBy { it.country }.map { it.value.first() }.map {

            val promoteURL = "https://www.${it.domain}?affid=${agent.promoteCode}"
            val mobilePromoteURL = "https://www.${it.domain}/m?affid=${agent.promoteCode}"

            AgentValue.PromoteVo(country = it.country, promoteURL = promoteURL, mobilePromoteURL = mobilePromoteURL)
        }

        val mainSite = webSiteService.getDataByBossId(bossId = -1).first { it.clientId == bossId && it.country == Country.Default }
//        val defaultClient = clientService.getMainClient(bossId = bossId) ?: error("")
//        val defaultSite = sites.first { it.clientId == defaultClient.id }
        val subAgentPromoteUrl = "https://aff.${mainSite.domain}?affid=${agent.promoteCode}"

        // 导航页
        val guideUrl = "https://www.${mainSite.domain}"


        return AgentValue.AgentInfo(balance = wallet.balance, subAgentCount = agentCount, memberCount = memberCount,
                subAgentCommission = agentMonthReport.agentCommission, memberCommission = agentMonthReport.memberCommission,
                agencyMonthFee = agent.agencyMonthFee, urls = urls, subAgentPromoteUrl = subAgentPromoteUrl, guideUrl = guideUrl,
                username = agent.username, phone = agent.phone, promoteCode = agent.promoteCode, name = agent.name,
                createdTime = agent.createdTime)
    }

    @GetMapping("/contactUs")
    override fun contactUs(): Contacts {

        val list = contactService.list(clientId = getClientId())
                .filter { it.role == Role.Agent }
                .filter { it.status == Status.Normal }

        val contacts = list.groupBy { it.type }
        val wechatContact = contacts[ContactType.Wechat]?.let { MathUtil.getRandom(it) }
        val whatContact = contacts[ContactType.Whatsapp]?.let { MathUtil.getRandom(it) }

        val  facebook = list.firstOrNull { it.type == ContactType.Facebook }
        val  youTuBe = list.firstOrNull { it.type == ContactType.YouTuBe }
        val  instagram = list.firstOrNull { it.type == ContactType.Instagram }

        return Contacts(wechatContact = wechatContact, whatsappContact = whatContact, facebook = facebook, youtube = youTuBe,
                instagram = instagram)
    }

    @GetMapping("/sub")
    override fun subAgents(): List<AgentValue.SubAgentVo> {

        val current = this.current()
        return analysisDao.subAgents(bossId = current.bossId, clientId = current.clientId, agentId =  current.id)
    }

    @GetMapping("/commission")
    override fun commissions(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") endDate:  LocalDate
    ): List<AgentValue.AgentCommissionVo> {

        val current = this.current()

        val reportQuery = AgentReportValue.AgentMonthQuery(bossId = current.bossId, clientId = current.clientId,  agentId = current.id)
        return agentMonthReportDao.query(reportQuery).map {
            AgentValue.AgentCommissionVo(day = it.day, totalDeposit = it.totalDeposit, totalWithdraw = it.totalWithdraw, totalBet = it.totalBet,
                    payout = it.payout,  totalRebate = it.totalRebate, totalPromotion = it.totalPromotion, newMemberCount = it.newMemberCount,
                    subAgentCommission = it.agentCommission, memberCommission = it.memberCommission, agentId = it.agentId, username = it.username)
        }
    }


    @GetMapping("/commission/sub")
    override fun subCommissions(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") endDate:  LocalDate,
            @RequestParam("superiorAgentId") superiorAgentId: Int
    ): List<AgentValue.AgentCommissionVo> {
        val current = this.current()
        val reportQuery = AgentReportValue.AgentMonthQuery(bossId = current.bossId, clientId = current.clientId,  superiorAgentId = superiorAgentId)
        return agentMonthReportDao.query(reportQuery).map {
            AgentValue.AgentCommissionVo(day = it.day, totalDeposit = it.totalDeposit, totalWithdraw = it.totalWithdraw, totalBet = it.totalBet,
                    payout = it.payout,  totalRebate = it.totalRebate, totalPromotion = it.totalPromotion, newMemberCount = it.newMemberCount,
                    subAgentCommission = it.agentCommission, memberCommission = it.memberCommission, agentId = it.agentId, username = it.username)
        }
    }

    @GetMapping("/commission/member")
    override fun memberCommissions(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") endDate:  LocalDate,
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

            AgentValue.MemberCommissionVo(username = newUsername, totalBet = it.totalBet, payout = it.payout, totalRebate = it.rebateAmount,
                    totalPromotion = it.promotionAmount)
        }
    }

    @GetMapping("/i18n")
    override fun i18nContentConfig(@RequestParam("configType") configType: I18nConfig): I18nContent.DefaultContentI18n {

        val mainClient = getMainClient()
        val language = getHeaderLanguage()

        val list =  i18nContentService.getConfigType(clientId = mainClient.id, configType = configType)
        val content = list.firstOrNull { it.language == language } ?: list.firstOrNull { it.language == Language.EN }
        return content?.let {
            it.getII18nContent(objectMapper = objectMapper) as I18nContent.DefaultContentI18n
        } ?: I18nContent.DefaultContentI18n(title = "hi", subTitle = "hi, sub title", content = "this is content")

    }
}