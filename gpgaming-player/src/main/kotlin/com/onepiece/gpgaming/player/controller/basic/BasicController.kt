package com.onepiece.gpgaming.player.controller.basic

import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Client
import com.onepiece.gpgaming.beans.model.WebSite
import com.onepiece.gpgaming.beans.value.internet.web.PlatformMemberVo
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.core.service.GamePlatformService
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.core.service.PlatformMemberService
import com.onepiece.gpgaming.core.service.WebSiteService
import com.onepiece.gpgaming.games.GameApi
import com.onepiece.gpgaming.games.value.ClientAuthVo
import com.onepiece.gpgaming.player.jwt.JwtUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest


abstract class BasicController {

    private val log = LoggerFactory.getLogger(BasicController::class.java)

    @Autowired
    lateinit var platformMemberService: PlatformMemberService

    @Autowired
    lateinit var platformBindService: PlatformBindService

    @Autowired
    lateinit var gameApi: GameApi

    @Autowired
    lateinit var webSiteService: WebSiteService

    @Autowired
    lateinit var gamePlatformService: GamePlatformService

    @Autowired
    lateinit var clientService: ClientService

    fun getRequest(): HttpServletRequest {
        return (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
    }

    private fun getRequestPath(): String {
        val request = this.getRequest()
        return request.requestURL.toString()
    }


    fun getWebSite(): WebSite {
        return webSiteService.match(this.getRequestPath())
    }

    fun getBossId(): Int {

        val requestPath = this.getRequestPath()
        if (requestPath.contains("gpgaming88.com") || requestPath.contains("localhost")) return 8


        val site = getWebSite()
        return if (site.country == Country.Default) site.clientId else site.bossId
    }

    fun getClientId(): Int {
        val site = getWebSite()
        return site.clientId
    }

    fun getMainClient(): Client {
        val bossId = this.getBossId()
        return clientService.getMainClient(bossId = bossId) ?: error(OnePieceExceptionCode.SYSTEM)
    }


//    fun getClientIdByDomain(): Int {
//        val request = this.getRequest()
//        val url = request.requestURL.toString()
//        return webSiteService.match(url).clientId
//    }
//
//    fun getBossIdByDomain(): Int {
//        val request = this.getRequest()
//        val url = request.requestURL.toString()
//        return webSiteService.matchReturnBossId(url)
//    }
//
//    fun getBossIdByGuide(): Int {
//        val request = this.getRequest()
//        val url = request.requestURL.toString()
//
//        return if (url.contains("gpgaming88.com") || url.contains("localhost")) { // TODO 测试环境写死
//            8
//        } else {
//            webSiteService.match(url).clientId
//        }
//    }
//
//     fun getSiteByDomain(): WebSite {
//         val request = this.getRequest()
//         val url = request.requestURL.toString()
//         return webSiteService.match(url)
//     }

    fun getHeaderLanguage(): Language {
        val request = this.getRequest()
        return try {
            request.getHeader("language").let {
                Language.valueOf(it)
            }
        } finally {
            Language.EN
        }
    }

    fun getLanguageAndLaunchFormHeader(): Pair<Language, LaunchMethod> {
        return this.getHeaderLanguage() to this.getHeaderLaunch()
    }

    fun getHeaderLaunch(): LaunchMethod {
        return this.getRequest().getHeader("launch").let {
            LaunchMethod.valueOf(it)
        }
    }

    fun current(): JwtUser {
        try {
            return SecurityContextHolder.getContext().authentication.principal as JwtUser
        } catch (e: Exception) {
            error("无法获得当前用户")
        }
    }

    fun currentClientIdAndMemberId():Pair<Int, Int> {
        val member = current()
        return member.clientId to member.id
    }

    @Synchronized
    fun getPlatformMember(platform: Platform, member: JwtUser, code: Int = 0): PlatformMemberVo {
        val platforms = platformMemberService.myPlatforms(memberId = member.id)
        val platformMember = platforms.find { platform == it.platform }

        // 判断平台是否维护
        check(gamePlatformService.all().first { it.platform == platform }.status == Status.Normal) { OnePieceExceptionCode.PLATFORM_MAINTAIN }


        if (code > 3) error(OnePieceExceptionCode.SYSTEM)

        log.info("用户名：${member.username}, 获得平台用户：$platform, code = $code")
        if (platformMember == null) {
            log.info("用户名：${member.username}, 开始注册平台用户：$platform, code = $code" + "")


//            gameApi(clientId = member.clientId, memberId = member.id, platform = platform, name = member.musername)
            gameApi.register(clientId = member.clientId, memberId = member.id, platform = platform, name = member.username)

            return this.getPlatformMember(platform, member, code + 1)
        }

        return platformMember
    }

    fun startRegister(clientId: Int, memberId: Int, platform: Platform, name: String) = runBlocking {
        val x = launch {
            gameApi.register(clientId = clientId, memberId = memberId, platform = platform, name = name)
        }
        x.join()
    }


    fun getClientAuthVo(platform: Platform): ClientAuthVo {
        return when (platform) {
            Platform.Kiss918 -> {
                val bind = platformBindService.findClientPlatforms(current().clientId).find { it.platform == platform }!!
                ClientAuthVo.ofKiss918(bind.username)
            }
            else -> ClientAuthVo.empty()
        }
    }

    fun currentUsername(): String {
        return this.current().username.split("@")[3]
    }

}