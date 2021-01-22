package com.onepiece.gpgaming.player.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.RegisterSource
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.model.PromotionRules
import com.onepiece.gpgaming.beans.model.token.PlaytechClientToken
import com.onepiece.gpgaming.beans.value.database.LoginValue
import com.onepiece.gpgaming.beans.value.database.MemberCo
import com.onepiece.gpgaming.beans.value.database.MemberIntroduceValue
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.core.dao.AnalysisDao
import com.onepiece.gpgaming.core.service.ClientConfigService
import com.onepiece.gpgaming.core.service.I18nContentService
import com.onepiece.gpgaming.core.service.LevelService
import com.onepiece.gpgaming.core.service.MarketService
import com.onepiece.gpgaming.core.service.MemberIntroduceService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.PromotionService
import com.onepiece.gpgaming.core.service.SmsContentService
import com.onepiece.gpgaming.core.service.VipService
import com.onepiece.gpgaming.core.utils.MarketUtil
import com.onepiece.gpgaming.player.controller.basic.BasicController
import com.onepiece.gpgaming.player.controller.chain.ChainUtil
import com.onepiece.gpgaming.player.controller.value.ChangePwdReq
import com.onepiece.gpgaming.player.controller.value.CheckUsernameResp
import com.onepiece.gpgaming.player.controller.value.LoginByAdminReq
import com.onepiece.gpgaming.player.controller.value.LoginByAdminResponse
import com.onepiece.gpgaming.player.controller.value.LoginReq
import com.onepiece.gpgaming.player.controller.value.LoginResp
import com.onepiece.gpgaming.player.controller.value.PlatformMemberUo
import com.onepiece.gpgaming.player.controller.value.PlatformMemberVo
import com.onepiece.gpgaming.player.controller.value.RegisterReq
import com.onepiece.gpgaming.player.controller.value.UserValue
import com.onepiece.gpgaming.player.jwt.AuthService
import com.onepiece.gpgaming.player.jwt.JwtUser
import com.onepiece.gpgaming.core.email.EmailSMTPService
import com.onepiece.gpgaming.player.sms.SmsService
import com.onepiece.gpgaming.utils.RequestUtil
import com.onepiece.gpgaming.utils.StringUtil
import eu.bitwalker.useragentutils.DeviceType
import eu.bitwalker.useragentutils.UserAgent
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime


@RestController
@RequestMapping("/user")
class UserApiController(
        private val memberService: MemberService,
        private val authService: AuthService,
        private val levelService: LevelService,
        private val passwordEncoder: PasswordEncoder,
        private val chainUtil: ChainUtil,
        private val vipService: VipService,
        private val marketUtil: MarketUtil,
        private val smsService: SmsService,
        private val marketService: MarketService,
        private val clientConfigService: ClientConfigService,
        private val memberIntroduceService: MemberIntroduceService,
        private val promotionService: PromotionService,
        private val i18nContentService: I18nContentService,
        private val objectMapper: ObjectMapper,
        private val analysisDao: AnalysisDao,
        private val smsContentService: SmsContentService,
        private val emailSMTPService: EmailSMTPService
) : BasicController(), UserApi {

    companion object {
        private val IP_LIST = listOf(
                "127.0.0.1",
                "localhost",
                "185.232.92.67",
                "13.251.241.87",
                "18.136.230.58"
        )
        private const val HASH_CODE = "28b419c9-08aa-40d1-9bc1-ea59ddf751f0"
        private val log = LoggerFactory.getLogger(UserApiController::class.java)

    }

    fun getDeviceType(): String {
        // ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        val agentString: String = getRequest().getHeader("User-Agent")
        val userAgent = UserAgent.parseUserAgentString(agentString)
        val operatingSystem = userAgent.operatingSystem // 操作系统信息
        return when (operatingSystem.deviceType) {
            DeviceType.COMPUTER -> "PC"
            DeviceType.TABLET -> {
                when (agentString) {
                    "Android" -> "Android Pad"
                    "iOS",
                    "Darwin" -> "iPad"
                    else -> "Unknown"
                }
            }
            DeviceType.MOBILE -> {

                when (agentString) {
                    "Android" -> "Android"
                    "iOS",
                    "Darwin" -> "iOS"
                    else -> "Unknown"
                }
            }
            else -> "Unknown"
        }
    }

    @PostMapping
    override fun login(@RequestBody loginReq: LoginReq): LoginResp {

        val bossId = getBossId()
        log.info("bossId = $bossId")
        val launch = getHeaderLaunch()
        val language = getHeaderLanguage()

        val deviceType = this.getDeviceType()

        val loginValue = LoginValue(bossId = bossId, username = loginReq.username, password = loginReq.password, ip = RequestUtil.getIpAddress())
        val member = memberService.login(loginValue, deviceType)
        check(member.role == Role.Member) { OnePieceExceptionCode.LOGIN_FAIL }

        val client = clientService.get(id = member.clientId)
        val webSites = webSiteService.getDataByBossId(bossId = bossId)
        val clientSite = webSites.first { it.clientId == member.clientId && it.country == client.country && it.status == Status.Normal }


//        val webSites = webSiteService.all()
        val currentWebSite = webSites.first { getRequest().requestURL.contains(it.domain) }
//
//        val client = clientService.get(member.clientId)
//        val clientWebSite = webSites.first { it.bossId == bossId && it.clientId == member.clientId && it.country ==  }
//                .filter { it.bossId == bossId }.first { it.clientId == member.clientId }

        val (vipName, vipLogo) = if (member.vipId > 0) {
            val vip = vipService.get(vipId = member.vipId)
            vip.name to vip.logo
        } else {
            "-" to "-"
        }

        val isMobile = if (launch == LaunchMethod.Wap) "/m" else ""

        val clientConfig = clientConfigService.get(clientId = client.id)

        return if (currentWebSite.clientId == member.clientId) {

            //  推荐介绍活动
            val introduce = memberIntroduceService.get(memberId = member.id)
            val registerActivity = introduce?.registerActivity ?: true
            val depositActivity = introduce?.depositActivity ?: true
            val registerActivityVo = if (!registerActivity && clientConfig.introducePromotionId > 0) {
//                val clientConfig = clientConfigService.get(clientId = member.clientId)
                val promotion = promotionService.get(clientConfig.introducePromotionId)

                val contents = i18nContentService.getConfigType(clientId = member.clientId, configType = I18nConfig.Promotion)
                        .filter { it.id == promotion.id }
                val content = (contents.firstOrNull { it.id == promotion.id && it.language == language }
                        ?: contents.firstOrNull { it.id == promotion.id && it.language == Language.EN }?.getII18nContent(objectMapper = objectMapper))?.let { it as I18nContent.PromotionI18n }

                LoginResp.RegisterActivityVo(promotionId = promotion.id, amount = clientConfig.registerCommission,
                        platforms = promotion.platforms, title = content?.title ?: "")
            } else null


            val token = authService.login(bossId = bossId, clientId = member.clientId, username = loginReq.username, role = member.role)
            LoginResp(id = member.id, role = Role.Member, username = member.username, token = token, name = member.name, autoTransfer = member.autoTransfer,
                    domain = "https://www.${clientSite.domain}${isMobile}", country = client.country, successful = true, vipLogo = vipLogo, vipName = vipName,
                    levelId = member.levelId, vipId = member.vipId, registerActivity = registerActivity, depositActivity = depositActivity, registerActivityVo = registerActivityVo,
                    enableIntroduce = clientConfig.enableIntroduce, email = member.email ?: "-", phone = member.phone, birthday = member.birthday?.toString() ?: "-"
            )
        } else {
            LoginResp(id = member.id, role = Role.Member, username = member.username, token = "", name = member.name, autoTransfer = member.autoTransfer,
                    domain = "https://www.${clientSite.domain}${isMobile}", country = client.country, successful = false, vipLogo = vipLogo, vipName = vipName,
                    levelId = member.levelId, vipId = member.vipId, registerActivity = false, registerActivityVo = null, depositActivity = false,
                    enableIntroduce = clientConfig.enableIntroduce, email = member.email ?: "-", phone = member.phone, birthday = member.birthday?.toString() ?: "-"
            )
        }
    }

    @PostMapping("/login_from_admin")
    override fun login(@RequestBody req: LoginByAdminReq): LoginByAdminResponse {

        // 验证IP
        val ip = RequestUtil.getIpAddress()
        check(IP_LIST.contains(ip)) { OnePieceExceptionCode.SYSTEM }

        // 验证密码
        val pwdStr = "${req.time}:$HASH_CODE:${req.username}"
        val flag = passwordEncoder.matches(pwdStr, req.hash)
        check(flag) { OnePieceExceptionCode.SYSTEM }

        // 校验用户名
        val member = memberService.findByUsername(clientId = req.clientId, username = req.username)
        checkNotNull(member) { OnePieceExceptionCode.SYSTEM }

        // 域名跳转地址
        val client = clientService.get(req.clientId)
        val site = webSiteService.getDataByBossId(bossId = client.bossId).first { it.status == Status.Normal && it.country == client.country }

        // 登陆
        val token = authService.login(bossId = member.bossId, clientId = member.clientId, username = member.username, role = member.role)

        val loginPath = if (site.clientId == 100001) {
                    "https://www.unclejay66.com/#?t=$token"
        } else {
            "https://www.${site.domain}/#?t=$token"
        }

        return LoginByAdminResponse(loginPath = loginPath)
    }

    @GetMapping("/login/detail")
    override fun loginDetail(): LoginResp {

        val user = this.currentUser()
        val launch = getHeaderLaunch()

        val member = memberService.getMember(user.id)


        val authHeader = this.getRequest().getHeader("Authorization")
        val authToken = authHeader.substring("Bearer ".length) // The part after "Bearer "


        val webSite = webSiteService.all().first { it.clientId == member.clientId }
        val client = clientService.get(member.clientId)



        val (vipName, vipLogo) = if (member.vipId > 0) {
            val vip = vipService.get(vipId = member.vipId)
            vip.name to vip.logo
        } else {
            "-" to "-"
        }

        val config = clientConfigService.get(clientId = client.id)


        val isMobile = if (launch == LaunchMethod.Wap) "/m" else ""
        return LoginResp(id = member.id, role = Role.Member, username = member.username, token = authToken, name = member.name, autoTransfer = member.autoTransfer,
                domain = "https://www.${webSite.domain}${isMobile}", country = client.country, successful = true, levelId = member.levelId, vipName = vipName,
                vipLogo = vipLogo, vipId = member.vipId, registerActivity = false, depositActivity = false, registerActivityVo = null, enableIntroduce = config.enableIntroduce,
                email = member.email ?: "-", phone = member.phone, birthday = member.birthday?.toString() ?: "-"
        )

    }

    @PutMapping("/config")
    override fun upAutoTransfer(@RequestParam("autoTransfer") autoTransfer: Boolean) {
        val uo = MemberUo(id = current().id, autoTransfer = autoTransfer)
        memberService.update(uo)
    }

    @PutMapping
    override fun register(@RequestBody registerReq: RegisterReq): LoginResp {

        check(registerReq.country != Country.Default)

        val bossId = getBossId()
        val client = clientService.all().filter { it.bossId == bossId }.first { it.country == registerReq.country }
        val clientId = client.id

        val phone = registerReq.phone.let {
            val firstPhone = it.substring(0, 3)
            if (firstPhone == "600") {
                val lastPhone = it.substring(3, it.length)
                "60$lastPhone"
            } else {
                it
            }
        }

        // 检查手机号是否存在
        val pm = memberService.findByBossIdAndPhone(bossId = bossId, phone = phone)
        check(pm == null) { "phone is exist" }

        // 代理
        val affid = registerReq.affid ?: "10"
        val (source, id) = RegisterSource.split(affid)
        val agent = when (source) {
            RegisterSource.Agent -> memberService.getMember(id = id)
            else -> memberService.getDefaultAgent(bossId = bossId)
        }


        val defaultLevel = levelService.getDefaultLevel(clientId = clientId)

        val saleId = if (source == RegisterSource.Sale) id else -1
        val marketId = if (source == RegisterSource.Market) id else -1
        val introduceId = if (source == RegisterSource.Introduce) id else -1

        val memberCo = MemberCo(clientId = clientId, username = registerReq.username, password = registerReq.password, safetyPassword = registerReq.safetyPassword,
                levelId = defaultLevel.id, name = registerReq.name, phone = phone, promoteCode = affid, bossId = bossId, agentId = agent.id,
                role = Role.Member, formal = true, saleId = saleId, registerIp = RequestUtil.getIpAddress(), birthday = registerReq.birthday,
                email = registerReq.email, marketId = marketId, introduceId = introduceId)
        memberService.create(memberCo)

        // 通知pv
        chainUtil.clickRv(registerReq.chainCode)

        // 通知
        if (marketId != -1) {
            marketUtil.addRV(clientId = clientId, marketId = marketId)
        }

        // 发送短信
        val messageTemplate = if (marketId != -1) {
            val market = marketService.get(id = marketId)
            marketUtil.addRV(clientId = clientId, marketId = marketId)
            market.messageTemplate.replace("\${code}", market.promotionCode)
        } else {
            clientConfigService.get(clientId = clientId).registerMessageTemplate
        }
        smsService.send(clientId = clientId, mobile = registerReq.phone, message = messageTemplate.replace("\${username}", registerReq.username))


        try {
            if (registerReq.email != null && registerReq.email.contains("@") && bossId == 10000) {
                emailSMTPService.send(clientId = clientId, username = registerReq.username, email = registerReq.email)
            }
        } catch (e: Exception) {

        }

        val loginReq = LoginReq(username = registerReq.username, password = registerReq.password)
        return this.login(loginReq)
    }

    @GetMapping("/market/view")
    override fun addMarketView(@RequestParam("marketId") marketId: Int) {
        val clientId = this.getClientId()
        marketUtil.addPV(clientId = clientId, marketId = marketId)
    }

    @GetMapping("/country")
    override fun countries(): List<Country> {

        val bossId = getBossId()
        val clientId = getClientId()

        val clients = clientService.all().filter { it.bossId == bossId }

        return clients.filter { it.country != Country.Default }.map {

            if (it.id == clientId) {
                0 to it.country
            } else {
                1 to it.country
            }
        }.sortedBy { it.first }
                .map { it.second }
    }

    @GetMapping("/check/{username}")
    override fun checkUsername(@PathVariable("username") username: String): CheckUsernameResp {
        val bossId = getBossId()

        val exist = memberService.findByBossIdAndUsername(bossId, username) != null
        return CheckUsernameResp(exist)
    }

    @GetMapping("/check/phone/{phone}")
    override fun checkPhone(@PathVariable("phone") phone: String): CheckUsernameResp {

        val newPhone = phone.let {
            val firstPhone = it.substring(0, 3)
            if (firstPhone == "600") {
                val lastPhone = it.substring(3, it.length)
                "60$lastPhone"
            } else {
                it
            }
        }

        val bossId = getBossId()
        val exist = memberService.findByBossIdAndPhone(bossId, newPhone) != null
        return CheckUsernameResp(exist)
    }

    @GetMapping("/current")
    fun currentUser(): JwtUser {
        return current()
    }

    @PutMapping("/password")
    override fun changePassword(@RequestBody changePwdReq: ChangePwdReq) {
        val memberId = current().id
        val memberUo = MemberUo(id = memberId, oldPassword = changePwdReq.oldPassword, password = changePwdReq.password)
        memberService.update(memberUo)
    }

    @PutMapping("/change/info")
    override fun changeUserInfo(@RequestBody uo: UserValue.UserInfoUo) {

        val user = this.currentUser()

        val memberUo = MemberUo(id = user.id, email = uo.email, birthday = uo.birthday)
        memberService.update(memberUo)
    }

    @GetMapping("/platform")
    override fun platformUsers(): List<PlatformMemberVo> {
        val current = this.currentUser()
        val platformMembers = platformMemberService.findPlatformMember(memberId = current.id)




        return platformMembers.map {

            val sort = when (it.platform) {
                Platform.Joker -> 1
                Platform.Kiss918 -> 2
                Platform.Pussy888 -> 3
                Platform.AllBet -> 4
                Platform.DreamGaming -> 5
                else -> 100
            }

            val username = when (it.platform) {
                Platform.PlaytechLive, Platform.PlaytechSlot -> {

                    val bind = platformBindService.findClientPlatforms(clientId = current.clientId).first { it.platform == Platform.PlaytechSlot }
                    val clientToken = bind.clientToken as PlaytechClientToken

                    "${clientToken.prefix}_${it.username}".toUpperCase()
                }
                else -> it.username
            }

            PlatformMemberVo(id = it.id, platform = it.platform, username = username, password = it.password, sort = sort)
        }.sortedBy { it.sort }
    }

    @PutMapping("/platform")
    override fun platformUser(@RequestBody platformMemberUo: PlatformMemberUo) {

        val current = this.currentUser()
        val platformMemberVo = getPlatformMember(platformMemberUo.platform, current)

        if (platformMemberUo.platform != Platform.Mega) {
            gameApi.updatePassword(clientId = current.clientId, platform = platformMemberUo.platform, username = platformMemberVo.platformUsername,
                    password = platformMemberUo.password)
        }

        platformMemberService.updatePassword(id = platformMemberVo.id, password = platformMemberUo.password)
    }

    @GetMapping("/introduce")
    override fun myIntroduceDetail(): UserValue.MyIntroduceDetail {
        val user = this.currentUser()

        val config = clientConfigService.get(clientId = user.clientId)
        if (!config.enableIntroduce) {
            return UserValue.MyIntroduceDetail.empty()
        }


        val introduceQuery = MemberIntroduceValue.MemberIntroduceQuery(introduceId = user.id)
        val data = memberIntroduceService.list(introduceQuery)

        val introduceCount = data.count()
        val overIntroduceCount = data.count { it.depositActivity }

        val introduceCommission = data.sumByDouble { it.introduceCommission.toDouble() }
                .toBigDecimal()
                .setScale(2, 2)


        val bet = if (config.introducePromotionId == -1) {
            BigDecimal.ZERO
        } else {
            val promotion = promotionService.get(config.introducePromotionId)
            when (promotion.rule) {
                is PromotionRules.BetRule -> {
                    val rule = promotion.rule as PromotionRules.BetRule
                    config.registerCommission.multiply(rule.betMultiple)
                }
                else -> {
                    val rule = promotion.rule as PromotionRules.WithdrawRule
                    config.registerCommission.multiply(rule.transferMultiplied)
                }
            }
        }

        val webSite = webSiteService.getDataByBossId(bossId = user.bossId).first { it.clientId == user.clientId }
        val affid = RegisterSource.splice(source = RegisterSource.Introduce, id = user.id)
        val link = "https://www.${webSite.domain}/#/?affid=$affid"
        return UserValue.MyIntroduceDetail(link = link, introduceCount = introduceCount, overIntroduceCount = overIntroduceCount, commission = introduceCommission,
                registerCommission = config.registerCommission, depositCommission = config.depositCommission, introducePromotionId = config.introducePromotionId, bet = bet,
                enableIntroduce = config.enableIntroduce, commissionPeriod = config.commissionPeriod, depositPeriod = config.depositPeriod, shareTemplate = config.shareTemplate)
    }

    @GetMapping("/introduce/list")
    override fun myIntroduceList(): List<UserValue.MyIntroduceVo> {

        val user = this.currentUser()

        val query = MemberIntroduceValue.MemberIntroduceQuery(introduceId = user.id)
        val data = memberIntroduceService.list(query = query)
        if (data.isEmpty()) return emptyList()

        val memberIds = data.map { it.memberId }
        val members = memberService.findByIds(ids = memberIds)
        val memberMap = members.map { it.id to it }.toMap()

        val deposit = analysisDao.findDeposits(memberIds = memberIds)

        return data.map { introduce ->
            val username = memberMap[introduce.memberId]?.username ?: ""
            val totalDeposit = deposit[introduce.memberId] ?: BigDecimal.ZERO
            UserValue.MyIntroduceVo(memberId = introduce.memberId, username = username, depositActivity = introduce.depositActivity, registerActivity = introduce.registerActivity,
                    introduceId = introduce.introduceId, totalDeposit = totalDeposit, introduceCommission = introduce.introduceCommission, createdTime = introduce.createdTime)
        }
    }

    @GetMapping("/regain")
    override fun sendMsgByRegain(@RequestParam("phone") phone: String): UserValue.RegainVo {

        val clientId = this.getClientId()
        val member = memberService.findByPhone(clientId, phone) ?: error(OnePieceExceptionCode.PHONE_NEVER_REGISTER)

        val config = clientConfigService.get(clientId = clientId)
        val code = StringUtil.generateNumNonce(6)
        val message = config.regainMessageTemplate.replace("\${code}", code)

        smsService.send(clientId = clientId, memberId = member.id, mobile = phone, message = message, code = code)

        return UserValue.RegainVo(memberId = member.id, phone = phone, username = member.username)
    }

    @PutMapping("/regain")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun regain(@RequestBody req: UserValue.RegainReq) {

        val smsContent = smsContentService.findLastSms(memberId = req.memberId)
        checkNotNull(smsContent) { OnePieceExceptionCode.SYSTEM }
        check(req.code == smsContent.code) { OnePieceExceptionCode.SMS_CODE_ERROR }

        // 检查时间
        val duration = Duration.between(smsContent.createdTime, LocalDateTime.now())
        check(duration.seconds <= 60) { OnePieceExceptionCode.SMS_CODE_TIMEOUT }

        // 修改妈妈
        val memberUo = MemberUo(id = req.memberId, password = req.password)
        memberService.update(memberUo)
    }

    @GetMapping("/sms/verify")
    override fun sendPhoneCode(@RequestParam("phone") phone: String): UserValue.PhoneCodeResp {
        val clientId = this.getClientId()

        val config = clientConfigService.get(clientId = clientId)
        val code = StringUtil.generateNumNonce(6)
//        val message = config.regainMessageTemplate.replace("\${code}", code)

        val message = "Your verification code: $code"

        val smsId = smsService.send(clientId = clientId, memberId = -1, mobile = phone, message = message, code = code)
        return UserValue.PhoneCodeResp(smsId = smsId.toString())

    }

    @PutMapping("/sms/verify")
    override fun verifyPhoneCode(@RequestBody req: UserValue.VerifyPhoneCodeReq) {
        val smsContent = smsContentService.get(id = req.msgId.toInt())
        check(req.code == smsContent.code) { OnePieceExceptionCode.SMS_CODE_ERROR }

        // 检查时间
        val duration = Duration.between(smsContent.createdTime, LocalDateTime.now())
        check(duration.seconds <= 60) { OnePieceExceptionCode.SMS_CODE_TIMEOUT }
    }
}