package com.onepiece.treasure.controller.basic

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.core.service.BetOrderService
import com.onepiece.treasure.core.service.PlatformMemberService
import com.onepiece.treasure.games.GameApi
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class DemoController(
        private val platformMemberService: PlatformMemberService,
        private val gameApi: GameApi,
        private val betOrderService: BetOrderService
) {

    @GetMapping("/bets")
    fun bets(
            @RequestParam("platform") platform: Platform
    ): Any {

        val startTime = LocalDateTime.now().minusDays(1)
        val endTime = LocalDateTime.now()

        return when (platform) {
            Platform.Kiss918, Platform.Pussy888, Platform.Mega, Platform.Bcs, Platform.AllBet -> {
                val platformMember = platformMemberService.find(memberId = 1, platform = platform) ?: return emptyList<Any>()
                gameApi.queryBetOrder(clientId = 1, platformUsername = platformMember.platformUsername, platform = platform, startTime = startTime, endTime = endTime)
            }
            else -> betOrderService.getBets(clientId = 1, memberId = 1, platform = platform)

        }
    }

}