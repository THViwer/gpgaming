package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.model.token.PlaytechClientToken
import com.onepiece.gpgaming.beans.value.database.LoginValue
import com.onepiece.gpgaming.beans.value.database.MemberCo
import com.onepiece.gpgaming.beans.value.database.MemberUo
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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserApiController(
        private val memberService: MemberService,
        private val authService: AuthService,
        private val levelService: LevelService
) : BasicController(), UserApi {

    @PostMapping
    override fun login(@RequestBody loginReq: LoginReq): LoginResp {
        //TODO 判断域名来选择厅主
        val clientId = getClientIdByDomain()

        val loginValue = LoginValue(clientId = clientId, username = loginReq.username, password = loginReq.password, ip = getIpAddress())
        val member = memberService.login(loginValue)

        val token = authService.login(clientId, loginReq.username)
        return LoginResp(id = member.id, role = Role.Member, username = member.username, token = token, name = member.name)

    }

    @PutMapping
    override fun register(@RequestBody registerReq: RegisterReq): LoginResp {

        val clientId = getClientIdByDomain()
        val defaultLevel = levelService.getDefaultLevel(clientId = clientId)

        val memberCo = MemberCo(clientId = clientId, username = registerReq.username, password = registerReq.password, safetyPassword = registerReq.safetyPassword,
                levelId = defaultLevel.id, name = registerReq.name, phone = registerReq.phone, promoteSource = registerReq.promoteSource)
        memberService.create(memberCo)

        val loginReq = LoginReq(username = registerReq.username, password = registerReq.password)
        return this.login(loginReq)
    }

    @GetMapping("/check/{username}")
    override fun checkUsername(@PathVariable("username") username: String): CheckUsernameResp {
        val clientId = getClientIdByDomain()
        val exist = memberService.findByUsername(clientId, username) != null
        return CheckUsernameResp(exist)
    }

    @GetMapping("/check/phone/{phone}")
    override fun checkPhone(@PathVariable("phone") phone: String): CheckUsernameResp {
        val clientId = getClientIdByDomain()
        val exist = memberService.findByPhone(clientId, phone) != null
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