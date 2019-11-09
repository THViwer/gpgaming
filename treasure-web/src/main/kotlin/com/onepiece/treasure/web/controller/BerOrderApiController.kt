package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.core.service.MemberService
import com.onepiece.treasure.core.service.PlatformMemberService
import com.onepiece.treasure.games.GameApi
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/bet")
class BerOrderApiController(
        private val gameApi: GameApi,
        private val platformMemberService: PlatformMemberService,
        private val memberService: MemberService
) : BasicController(), BetOrderApi {


    @GetMapping
    override fun bets(
            @RequestHeader("platform") platform: Platform,
            @RequestParam("username") username: String,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate): Any {


        val member = memberService.findByUsername(username) ?: return emptyList<Any>()
        return when (platform) {
            Platform.Joker, Platform.Cta666 -> gameApi.queryBetOrder(clientId = clientId, memberId = member.id, platform = platform, startDate = startDate, endDate = endDate)
            Platform.Kiss918, Platform.Sbo -> {
                val platformMember = platformMemberService.find(memberId = member.id, platform = platform) ?: return emptyList<Any>()
                gameApi.queryBetOrder(clientId = clientId, platformUsername = platformMember.platformUsername, platform = platform, startDate = startDate, endDate = endDate)
            }

            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

    }

}