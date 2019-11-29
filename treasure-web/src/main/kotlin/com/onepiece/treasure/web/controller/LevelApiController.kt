package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.database.LevelCo
import com.onepiece.treasure.beans.value.database.LevelUo
import com.onepiece.treasure.beans.value.database.WalletQuery
import com.onepiece.treasure.beans.value.internet.web.*
import com.onepiece.treasure.core.service.LevelService
import com.onepiece.treasure.core.service.MemberService
import com.onepiece.treasure.core.service.WalletService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/level")
class LevelApiController(
        private val levelService: LevelService,
        private val memberService: MemberService,
        private val walletService: WalletService
) : BasicController(), LevelApi {

    @GetMapping
    override fun all(): List<LevelVo> {
        val clientId = getClientId()

        val levelCountMap = memberService.getLevelCount(clientId)

        return levelService.all(clientId).map {
            val count = levelCountMap[it.id] ?: 0
            LevelVo(id = it.id, name = it.name, status = it.status, createdTime = it.createdTime, total = count)
        }
    }

    @GetMapping("/normal")
    override fun normalList(): List<LevelVo> {
        return all().filter { it.status == Status.Normal }
    }

    @PostMapping
    override fun create(@RequestBody levelCoReq: LevelCoReq) {
        val clientId = getClientId()
        val levelCo = LevelCo(clientId = clientId, name = levelCoReq.name)
        levelService.create(levelCo)
    }

    @PutMapping
    override fun update(@RequestBody levelUoReq: LevelUoReq) {
        val levelUo = LevelUo(id = levelUoReq.id, name = levelUoReq.name, status = levelUoReq.status)
        levelService.update(levelUo)
    }

    @GetMapping("/member")
    override fun findMembers(
            @RequestParam("username", required = false) username: String?,
            @RequestParam("levelId", required = false) levelId: Int?,
            @RequestParam("minBalance", required = false) minBalance: BigDecimal?,
            @RequestParam("maxBalance", required = false) maxBalance: BigDecimal?,
            @RequestParam("minTotalDepositBalance", required = false) minTotalDepositBalance: BigDecimal?,
            @RequestParam("maxTotalDepositBalance", required = false) maxTotalDepositBalance: BigDecimal?,
            @RequestParam("minTotalWithdrawBalance", required = false) minTotalWithdrawBalance: BigDecimal?,
            @RequestParam("maxTotalWithdrawBalance", required = false) maxTotalWithdrawBalance: BigDecimal?,
            @RequestParam("minTotalDepositFrequency", required = false) minTotalDepositFrequency: Int?,
            @RequestParam("maxTotalDepositFrequency", required = false) maxTotalDepositFrequency: Int?,
            @RequestParam("minTotalWithdrawFrequency", required = false) minTotalWithdrawFrequency: Int?,
            @RequestParam("maxTotalWithdrawFrequency", required = false) maxTotalWithdrawFrequency: Int?
    ): List<LevelMemberVo> {
        val clientId = getClientId()

        check(
                username == null
                        || minBalance == null || maxBalance == null
                        || minTotalDepositBalance == null || maxTotalDepositBalance == null
                        || minTotalWithdrawBalance == null || maxTotalWithdrawBalance == null
                        || minTotalDepositFrequency == null || maxTotalDepositFrequency == null
                        || minTotalWithdrawFrequency == null || maxTotalWithdrawFrequency == null) { OnePieceExceptionCode.QUERY_COUNT_TOO_SMALL}

        val memberId = when (username != null) {
            true -> memberService.findByUsername(username)?.id ?: return emptyList()
            false -> null
        }

        // 查询钱包情况
        val walletQuery = WalletQuery(clientId = clientId, memberId = memberId, minBalance = minBalance, minTotalDepositBalance = minTotalDepositBalance,
                minTotalDepositFrequency = minTotalDepositFrequency, minTotalWithdrawBalance = minTotalWithdrawBalance, minTotalWithdrawFrequency = minTotalWithdrawFrequency,
                maxBalance = maxBalance, maxTotalDepositBalance = maxTotalDepositBalance, maxTotalDepositFrequency = maxTotalDepositFrequency,
                maxTotalWithdrawBalance = maxTotalWithdrawBalance, maxTotalWithdrawFrequency = maxTotalWithdrawFrequency)
        val wallets = walletService.query(walletQuery)
        if (wallets.isEmpty()) return emptyList()
        val memberIds = wallets.map { it.memberId }
        val walletMap = wallets.map{ it.memberId to it}.toMap()

        // 查询用户信息
        val members = memberService.findByIds(levelId = levelId, ids = memberIds)

        // 查询用户层级
        val levels = levelService.all(clientId).map { it.id to it }.toMap()

        // 组装数据
        return members.filter { walletMap[it.id] != null }.map { member ->
            val wallet = walletMap[member.id] ?: error(OnePieceExceptionCode.DATA_FAIL)

            val level = levels[member.levelId]?: error(OnePieceExceptionCode.DATA_FAIL)

            LevelMemberVo(memberId = member.id, username = member.username, balance = wallet.balance, freezeBalance = wallet.freezeBalance, totalDepositBalance = wallet.totalDepositBalance,
                    totalWithdrawBalance = wallet.totalWithdrawBalance, totalGiftBalance = wallet.totalGiftBalance, totalWithdrawFrequency = wallet.totalWithdrawFrequency,
                    totalDepositFrequency = wallet.totalDepositFrequency, loginTime = member.loginTime, levelId = level.id, levelName = level.name)
        }


    }

    @PutMapping("/move")
    override fun move(@RequestBody levelMoveDo: LevelMoveDo) {
        val clientId = getClientId()

        //TODO 人数多的时候进行分组移动
        check(levelMoveDo.memberIds.isNotEmpty()) { OnePieceExceptionCode.MOVE_LEVEL_COUNT_ISZERO }
        check(levelMoveDo.memberIds.size <= 2000) { OnePieceExceptionCode.MOVE_LEVEL_COUNT_ISMAX }

        memberService.moveLevel(clientId = clientId, levelId = levelMoveDo.levelId, memberIds = levelMoveDo.memberIds)
    }

//    @GetMapping("/move/check/{sequence}")
//    override fun checkMove(@PathVariable sequence: String): LevelMoveCheckVo {
//        return LevelValueFactory.generatorLevelMoveCheckVo()
//    }
}