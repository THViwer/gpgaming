package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.database.MemberCo
import com.onepiece.treasure.beans.value.database.MemberQuery
import com.onepiece.treasure.beans.value.database.MemberUo
import com.onepiece.treasure.beans.value.internet.web.*
import com.onepiece.treasure.core.service.LevelService
import com.onepiece.treasure.core.service.MemberService
import com.onepiece.treasure.core.service.WalletService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/member")
class MemberApiController(
        private val memberService: MemberService,
        private val walletService: WalletService,
        private val levelService: LevelService
) : BasicController(), MemberApi {

    @GetMapping
    override fun query(
            @RequestParam(value = "username", required = false) username: String?,
            @RequestParam(value = "levelId", required = false) levelId: Int?,
            @RequestParam(value = "status", required = false) status: Status?,
            @RequestParam(defaultValue = "0") current: Int,
            @RequestParam(defaultValue = "10") size: Int
    ): MemberPage {
        val clientId = getClientId()

        val query = MemberQuery(clientId = clientId, startTime = null, endTime = null, username = username,
                levelId = levelId, status = status)
        val page = memberService.query(query, current, size)
        if (page.total == 0) return MemberPage(total = 0, data = emptyList())

        val levels = levelService.all(clientId).map { it.id to it }.toMap()

        val data = page.data.map {
            try {
                with(it) {
                    MemberVo(id = id, username = it.username, levelId = it.levelId, level = levels[it.levelId]?.name?: "",
                            balance = BigDecimal.ZERO, status = it.status, createdTime = createdTime,
                            loginIp = loginIp, loginTime = loginTime, name = it.name)
                }
            } catch (e: Exception) {
                println("$")
                null
            }
        }.filterNotNull()

        return MemberPage(total = page.total, data = data)
    }

    @PutMapping
    override fun update(@RequestBody memberUoReq: MemberUoReq) {
        val clientId = getClientId()

        val member = memberService.getMember(memberUoReq.id)
        check(member.clientId == clientId) { OnePieceExceptionCode.AUTHORITY_FAIL }

        val memberUo = MemberUo(id = memberUoReq.id, levelId = memberUoReq.levelId, password = memberUoReq.password,
                status = memberUoReq.status, name = memberUoReq.name)
        memberService.update(memberUo)
    }

    @PostMapping
    override fun create(@RequestBody memberCoReq: MemberCoReq) {
        val clientId = getClientId()

        val memberCo = MemberCo(clientId = clientId, username = memberCoReq.username, password = memberCoReq.password,
                safetyPassword = memberCoReq.safetyPassword, levelId = memberCoReq.levelId, name = memberCoReq.name, phone = memberCoReq.phone)
        memberService.create(memberCo)
    }

    @GetMapping("/wallet/{memberId}")
    override fun balance(
            @PathVariable(value = "memberId") memberId: Int
    ): WalletVo {

        val wallet = walletService.getMemberWallet(memberId)

        return with(wallet) {
            WalletVo(id = wallet.id, memberId = wallet.memberId, balance = balance, freezeBalance = freezeBalance,
                    totalGiftBalance = totalGiftBalance, totalDepositBalance = totalDepositBalance, totalWithdrawBalance = totalWithdrawBalance)
        }

//        return when (platform) {
//            Platform.Center -> walletVo
//            else -> {
//                TODO 从平台中查询余额
//                walletVo.copy(balance = BigDecimal.valueOf(100))
//            }

//        }
    }
}