package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.value.database.MemberCo
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.beans.value.database.WalletQuery
import com.onepiece.gpgaming.beans.value.internet.web.MemberCoReq
import com.onepiece.gpgaming.beans.value.internet.web.MemberPage
import com.onepiece.gpgaming.beans.value.internet.web.MemberUoReq
import com.onepiece.gpgaming.beans.value.internet.web.MemberVo
import com.onepiece.gpgaming.beans.value.internet.web.WalletVo
import com.onepiece.gpgaming.core.service.LevelService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.WalletService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): MemberPage {
        val clientId = getClientId()

        val query = MemberQuery(clientId = clientId, startTime = null, endTime = null, username = username,
                levelId = levelId, status = status)
        val page = memberService.query(query, current, size)
        if (page.total == 0) return MemberPage(total = 0, data = emptyList())

        val levels = levelService.all(clientId).map { it.id to it }.toMap()

        val ids = page.data.map { it.id }
        val walletQuery = WalletQuery(clientId = clientId, memberIds = ids)
        val memberMap = walletService.query(walletQuery).map { it.memberId to it }.toMap()

        val data = page.data.map {
            with(it) {
                MemberVo(id = id, username = it.username, levelId = it.levelId, level = levels[it.levelId]?.name ?: "error level",
                        balance = memberMap[it.id]?.balance ?: BigDecimal.valueOf(-1), status = it.status, createdTime = createdTime,
                        loginIp = loginIp, loginTime = loginTime, name = it.name, phone = it.phone)
            }
        }

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
        check(memberCoReq.levelId > 0) { OnePieceExceptionCode.DATA_FAIL }

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