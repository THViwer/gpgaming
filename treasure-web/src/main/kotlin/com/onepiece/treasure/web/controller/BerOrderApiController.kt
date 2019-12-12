package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.BetOrder
import com.onepiece.treasure.core.service.BetOrderService
import com.onepiece.treasure.core.service.MemberService
import com.onepiece.treasure.core.service.PlatformMemberService
import com.onepiece.treasure.games.GameApi
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.slot.MegaService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/bet")
class BerOrderApiController(
        private val gameApi: GameApi,
        private val platformMemberService: PlatformMemberService,
        private val memberService: MemberService,
        private val betOrderService: BetOrderService,
        private val megaService: MegaService
) : BasicController(), BetOrderApi {


    @GetMapping
    override fun bets(
            @RequestHeader("platform") platform: Platform,
            @RequestParam("username") username: String,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("endTime") endTime: LocalDateTime
    ): Any {

        val member = memberService.findByUsername(username) ?: return emptyList<String>()

        val clientId = getClientId()
        return when (platform) {
            Platform.Kiss918,
            Platform.Pussy888 -> {
                val platformMember = platformMemberService.find(memberId = member.id, platform = platform) ?: return emptyList<String>()
                val list = gameApi.queryBetOrder(clientId = clientId, platformUsername = platformMember.platformUsername,
                        platform = platform, startTime = startTime, endTime = endTime)
                return list.map {
                    BetOrder(id = -1, clientId = it.clientId, memberId = it.memberId, betTime = it.betTime, settleTime = it.settleTime, betAmount = it.betAmount,
                            winAmount = it.winAmount, mark = true, orderId = it.orderId, createdTime = it.betTime, originData = it.originData, platform = it.platform)
                }
            }
            Platform.Mega -> {
                val platformMember = platformMemberService.find(memberId = member.id, platform = Platform.Mega) ?: return mapOf("url" to "")
                val bind = platformBindService.find(clientId = member.clientId, platform = Platform.Mega)

                val betOrderReq = GameValue.BetOrderReq(token = bind.clientToken, startTime = startTime, endTime = endTime, username = platformMember.platformUsername)
                val html = megaService.getBetOrderHtml(betOrderReq)

                return mapOf("url" to html)
            }
            else -> betOrderService.getBets(clientId = clientId, memberId = member.id, platform = platform)
        }

    }

}