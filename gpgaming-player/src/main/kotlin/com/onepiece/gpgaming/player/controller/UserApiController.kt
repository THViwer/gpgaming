package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.model.token.PlaytechClientToken
import com.onepiece.gpgaming.beans.value.database.LoginValue
import com.onepiece.gpgaming.beans.value.database.MemberCo
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.core.service.LevelService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.player.controller.basic.BasicController
import com.onepiece.gpgaming.player.controller.value.ChangePwdReq
import com.onepiece.gpgaming.player.controller.value.CheckUsernameResp
import com.onepiece.gpgaming.player.controller.value.LoginReq
import com.onepiece.gpgaming.player.controller.value.LoginResp
import com.onepiece.gpgaming.player.controller.value.PlatformMemberUo
import com.onepiece.gpgaming.player.controller.value.PlatformMemberVo
import com.onepiece.gpgaming.player.controller.value.RegisterReq
import com.onepiece.gpgaming.player.jwt.AuthService
import com.onepiece.gpgaming.player.jwt.JwtUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserApiController(
        private val memberService: MemberService,
        private val authService: AuthService,
        private val levelService: LevelService,
        private val clientService: ClientService
) : BasicController(), UserApi {

    @PostMapping
    override fun login(
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod,
            @RequestBody loginReq: LoginReq
    ): LoginResp {

        val bossId = getBossIdByDomain()

        val loginValue = LoginValue(bossId = bossId, username = loginReq.username, password = loginReq.password, ip = getIpAddress())
        val member = memberService.login(loginValue)

        val webSites = webSiteService.all()
        val webSite = webSites.first { getRequest().requestURL.contains(it.domain) }
        val client = clientService.get(member.clientId)

        val isMobile = if (launch == LaunchMethod.Wap) "/m" else ""

        return if (webSite.clientId == member.clientId) {
            val token = authService.login(clientId = member.clientId, username = loginReq.username)
            LoginResp(id = member.id, role = Role.Member, username = member.username, token = token, name = member.name, autoTransfer = member.autoTransfer,
                    domain = "https://www.${webSite.domain}${isMobile}", country = client.country, successful = true)
        } else {
            LoginResp(id = member.id, role = Role.Member, username = member.username, token = "", name = member.name, autoTransfer = member.autoTransfer,
                    domain = "https://www.${webSite.domain}${isMobile}", country = client.country, successful = false)
        }
    }


    @GetMapping("/login/detail")
    override fun loginDetail(
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod
    ): LoginResp {

        val user = this.currentUser()

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
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod,
            @RequestBody registerReq: RegisterReq
    ): LoginResp {

        check(registerReq.country != Country.Default)

        val bossId = getBossIdByDomain()
        val client = clientService.all().filter { it.bossId == bossId }.first { it.country == registerReq.country }
        val clientId = client.id

        val defaultLevel = levelService.getDefaultLevel(clientId = clientId)
        val memberCo = MemberCo(clientId = clientId, username = registerReq.username, password = registerReq.password, safetyPassword = registerReq.safetyPassword,
                levelId = defaultLevel.id, name = registerReq.name, phone = registerReq.phone, promoteSource = registerReq.promoteSource, bossId = bossId)
        memberService.create(memberCo)

        val loginReq = LoginReq(username = registerReq.username, password = registerReq.password)
        return this.login(launch, loginReq)
    }

    @GetMapping("/country")
    override fun countries(): List<Country> {

        val clientId = getClientIdByDomain()
        val client = clientService.get(clientId)

        val bossId = client.bossId
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
        val bossId = getBossIdByDomain()

        val exist = memberService.findByBossIdAndUsername(bossId, username) != null
        return CheckUsernameResp(exist)
    }

    @GetMapping("/check/phone/{phone}")
    override fun checkPhone(@PathVariable("phone") phone: String): CheckUsernameResp {
        val bossId = getBossIdByDomain()
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


        val bind = platformBindService.findClientPlatforms(clientId = current.clientId).first { it.platform == Platform.PlaytechSlot }
        val clientToken = bind.clientToken as PlaytechClientToken

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