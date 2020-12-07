package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.BetOrder
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.service.BetOrderService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.PlatformMemberService
import com.onepiece.gpgaming.games.GameApi
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.slot.MegaService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
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

        val clientId = getClientId()
        val member = memberService.findByUsername(clientId, username) ?: return emptyList<String>()
        return when (platform) {
            Platform.Kiss918,
            Platform.Pussy888 -> {
                val platformMember = platformMemberService.find(memberId = member.id, platform = platform) ?: return emptyList<String>()
                val list = gameApi.queryBetOrder(clientId = clientId, platformUsername = platformMember.platformUsername,
                        platform = platform, startTime = startTime, endTime = endTime, memberId = member.id)
                return list.map {
                    BetOrder(id = -1, clientId = it.clientId, memberId = it.memberId, betTime = it.betTime, settleTime = it.settleTime, betAmount = it.betAmount,
                            payout = it.payout, mark = true, orderId = it.orderId, createdTime = it.betTime, originData = it.originData, platform = it.platform,
                            status = Status.Normal, validAmount = it.betAmount)
                }
            }
            Platform.Mega -> {
                val platformMember = platformMemberService.find(memberId = member.id, platform = Platform.Mega) ?: return mapOf("url" to "")
                val bind = platformBindService.find(clientId = member.clientId, platform = Platform.Mega)

                val betOrderReq = GameValue.BetOrderReq(token = bind.clientToken, startTime = startTime, endTime = endTime, username = platformMember.platformUsername)
                val html = megaService.getBetOrderHtml(betOrderReq)

                return mapOf("url" to html)
            }
            else -> {
                val query = BetOrderValue.BetOrderQuery(clientId = clientId, memberId = member.id, platform = platform, betStartTime = startTime, betEndTime = endTime)
                betOrderService.getBets(query = query)
            }
        }.sortedByDescending { it.betTime }
    }

    @GetMapping("/last500")
    override fun last500(@RequestParam("username") username: String): List<BetOrder> {

        val clientId = getClientId()
        val member = memberService.findByUsername(clientId, username) ?: return emptyList()

        val endDate = LocalDate.now()
        val startDate = endDate.minusWeeks(1)
        return betOrderService.last500(clientId = getClientId(), memberId = member.id, startDate = startDate, endDate = endDate)
    }
}