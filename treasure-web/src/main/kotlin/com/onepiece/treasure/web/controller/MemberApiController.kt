package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.database.MemberQuery
import com.onepiece.treasure.beans.value.database.MemberUo
import com.onepiece.treasure.beans.value.internet.web.*
import com.onepiece.treasure.core.service.MemberService
import com.onepiece.treasure.core.service.WalletService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime

@RestController
@RequestMapping("/member")
class MemberApiController(
        private val memberService: MemberService,
        private val walletService: WalletService
) : BasicController(), MemberApi {

    @GetMapping
    override fun query(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endTime") endTime: LocalDateTime,
            @RequestParam(value = "username", required = false) username: String?,
            @RequestParam(value = "levelId", required = false) levelId: Int?,
            @RequestParam(value = "status", required = false) status: Status?,
            @RequestParam(defaultValue = "0") current: Int,
            @RequestParam(defaultValue = "10") size: Int
    ): MemberPage {

        val query = MemberQuery(clientId = clientId, startTime = startTime, endTime = endTime, username = username,
                levelId = levelId, status = status)
        val page = memberService.query(query, current, size)
        if (page.total == 0) return MemberPage(total = 0, data = emptyList())

        val data = page.data.map {
            with(it) {
                MemberVo(id = id, username = it.username, levelId = it.levelId, level = "", name = "name", balance = BigDecimal.ZERO, status = it.status,
                        createdTime = createdTime, loginIp = loginIp, loginTime = loginTime)
            }
        }

        return MemberPage(total = page.total, data = data)
    }

    @PutMapping
    override fun change(
            @RequestBody memberUoReq: MemberUoReq
    ) {

        val member = memberService.getMember(memberUoReq.id)
        check(member.clientId == clientId) { OnePieceExceptionCode.AUTHORITY_FAIL }

        val memberUo = MemberUo(id = memberUoReq.id, levelId = memberUoReq.levelId, password = memberUoReq.password,
                status = memberUoReq.status)
        memberService.update(memberUo)
    }

    @GetMapping("/wallet/{memberId}")
    override fun balance(
            @PathVariable(value = "memberId") memberId: Int,
            @RequestParam(value = "platform") platform: Platform
    ): WalletVo {

        val wallet = walletService.getMemberWallet(memberId)

        val walletVo = with(wallet) {
            WalletVo(id = wallet.id, memberId = wallet.memberId, platform = platform, balance = balance, freezeBalance = freezeBalance,
                    currentBet = currentBet, demandBet = demandBet, totalGiftBalance = totalGiftBalance, totalBet = totalBet,
                    totalFrequency = totalFrequency, totalBalance = totalBalance, giftBalance = giftBalance)
        }

        return when (platform) {
            Platform.Center -> walletVo
            else -> {
                //TODO 从平台中查询余额
                walletVo.copy(balance = BigDecimal.valueOf(100))
            }

        }
    }
}