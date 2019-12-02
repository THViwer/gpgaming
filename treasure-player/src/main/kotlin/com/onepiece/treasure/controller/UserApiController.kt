package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Role
import com.onepiece.treasure.beans.value.database.LoginValue
import com.onepiece.treasure.beans.value.database.MemberCo
import com.onepiece.treasure.beans.value.database.MemberUo
import com.onepiece.treasure.controller.basic.BasicController
import com.onepiece.treasure.controller.value.*
import com.onepiece.treasure.core.service.LevelService
import com.onepiece.treasure.core.service.MemberService
import com.onepiece.treasure.core.service.PlatformMemberService
import com.onepiece.treasure.jwt.AuthService
import com.onepiece.treasure.jwt.JwtUser
import org.springframework.web.bind.annotation.*

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

        val loginValue = LoginValue(username = loginReq.username, password = loginReq.password, ip = getIpAddress())
        val member = memberService.login(loginValue)

        val token = authService.login(loginReq.username)
        return LoginResp(id = member.id, role = Role.Member, username = member.username, token = token, name = member.name)

    }

    @PutMapping
    override fun register(@RequestBody registerReq: RegisterReq): LoginResp {

        val clientId = getClientIdByDomain()
        val defaultLevel = levelService.getDefaultLevel(clientId = clientId)

        val memberCo = MemberCo(clientId = clientId, username = registerReq.username, password = registerReq.password, safetyPassword = registerReq.safetyPassword,
                levelId = defaultLevel.id, name = registerReq.name)
        memberService.create(memberCo)

        val loginReq = LoginReq(username = registerReq.username, password = registerReq.password)
        return this.login(loginReq)
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

            PlatformMemberVo(id = it.id, platform = it.platform, username = it.username, password = it.password, sort = sort)
        }.sortedBy { it.sort }
    }

    @PutMapping("/platform")
    override fun platformUser(@RequestBody platformMemberUo: PlatformMemberUo) {

        val current = this.currentUser()
        val platformMemberVo = getPlatformMember(platformMemberUo.platform)

        gameApi.updatePassword(clientId = current.clientId, platform = platformMemberUo.platform, username = platformMemberVo.platformUsername,
                password = platformMemberUo.password)

        platformMemberService.updatePassword(id = platformMemberVo.id, password = platformMemberUo.password)
    }
}