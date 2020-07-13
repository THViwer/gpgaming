package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.PlaytechClientToken
import com.onepiece.gpgaming.beans.value.database.LoginValue
import com.onepiece.gpgaming.beans.value.database.MemberCo
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.core.service.LevelService
import com.onepiece.gpgaming.core.service.MemberInfoService
import com.onepiece.gpgaming.core.service.MemberService
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
import com.onepiece.gpgaming.player.jwt.AuthService
import com.onepiece.gpgaming.player.jwt.JwtUser
import com.onepiece.gpgaming.utils.RequestUtil
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserApiController(
        private val memberService: MemberService,
        private val authService: AuthService,
        private val levelService: LevelService,
        private val memberInfoService: MemberInfoService,
        private val passwordEncoder: PasswordEncoder,
        private val chainUtil: ChainUtil
) : BasicController(), UserApi {

    companion object {
        private val IP_LIST = listOf(
                "127.0.0.1",
                "localhost",
                "185.232.92.67",
                "13.251.241.87"
        )
        private const val HASH_CODE = "28b419c9-08aa-40d1-9bc1-ea59ddf751f0"
        private val log = LoggerFactory.getLogger(UserApiController::class.java)

    }


    @PostMapping
    override fun login(@RequestBody loginReq: LoginReq): LoginResp {

        val bossId = getBossId()
        log.info("bossId = $bossId")
        val launch = getHeaderLaunch()

        val loginValue = LoginValue(bossId = bossId, username = loginReq.username, password = loginReq.password, ip = RequestUtil.getIpAddress())
        val member = memberService.login(loginValue)
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


        val isMobile = if (launch == LaunchMethod.Wap) "/m" else ""

        return if (currentWebSite.clientId == member.clientId) {
            val token = authService.login(bossId = bossId, clientId = member.clientId, username = loginReq.username, role = member.role)
            LoginResp(id = member.id, role = Role.Member, username = member.username, token = token, name = member.name, autoTransfer = member.autoTransfer,
                    domain = "https://www.${clientSite.domain}${isMobile}", country = client.country, successful = true)
        } else {
            LoginResp(id = member.id, role = Role.Member, username = member.username, token = "", name = member.name, autoTransfer = member.autoTransfer,
                    domain = "https://www.${clientSite.domain}${isMobile}", country = client.country, successful = false)
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
        return LoginByAdminResponse(loginPath = "https://www.${site.domain}?t=$token")
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


        val isMobile = if (launch == LaunchMethod.Wap) "/m" else ""
        return LoginResp(id = member.id, role = Role.Member, username = member.username, token = authToken, name = member.name, autoTransfer = member.autoTransfer,
                domain = "https://www.${webSite.domain}${isMobile}", country = client.country, successful = true)

    }

    @PutMapping("/config")
    override fun upAutoTransfer(@RequestParam("autoTransfer") autoTransfer: Boolean) {
        val uo = MemberUo(id = current().id, autoTransfer = autoTransfer)
        memberService.update(uo)
    }

    @PutMapping
    override fun register(
            @RequestBody registerReq: RegisterReq
    ): LoginResp {

        check(registerReq.country != Country.Default)

        val bossId = getBossId()
        log.info("bossId = $bossId")
        log.info("clients = ${clientService.all()}")
        val client = clientService.all().filter { it.bossId == bossId }.first { it.country == registerReq.country }
        val clientId = client.id

        // 代理
        val agent = registerReq.promoteCode?.let {
            memberService.findByBossIdAndCode(bossId = bossId, promoteCode = registerReq.promoteCode)
        } ?: memberService.getDefaultAgent(bossId = bossId)

        val defaultLevel = levelService.getDefaultLevel(clientId = clientId)
        val memberCo = MemberCo(clientId = clientId, username = registerReq.username, password = registerReq.password, safetyPassword = registerReq.safetyPassword,
                levelId = defaultLevel.id, name = registerReq.name, phone = registerReq.phone, promoteCode = registerReq.promoteCode, bossId = bossId, agentId = agent.id,
                role = Role.Member, formal = true, saleId = registerReq.saleCode?.toInt(), registerIp = RequestUtil.getIpAddress())
        memberService.create(memberCo)

        // 通知pv
        chainUtil.clickRv(registerReq.chainCode)

        val loginReq = LoginReq(username = registerReq.username, password = registerReq.password)
        return this.login(loginReq)
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
        val bossId = getBossId()
        val exist = memberService.findByBossIdAndPhone(bossId, phone) != null
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

    @GetMapping("/platform")
    override fun platformUsers(): List<PlatformMemberVo> {
        val current = this.currentUser()
        val platformMembers = platformMemberService.findPlatformMember(memberId =  current.id)




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
}