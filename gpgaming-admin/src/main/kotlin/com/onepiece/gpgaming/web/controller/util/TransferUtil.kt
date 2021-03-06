package com.onepiece.gpgaming.web.controller.util

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PromotionCategory
import com.onepiece.gpgaming.beans.enums.PromotionPeriod
import com.onepiece.gpgaming.beans.enums.PromotionRuleType
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.enums.TransferState
import com.onepiece.gpgaming.beans.enums.WalletEvent
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.PlatformMember
import com.onepiece.gpgaming.beans.model.Promotion
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.beans.value.database.PlatformMemberTransferUo
import com.onepiece.gpgaming.beans.value.database.TransferOrderCo
import com.onepiece.gpgaming.beans.value.database.TransferOrderUo
import com.onepiece.gpgaming.beans.value.database.WalletUo
import com.onepiece.gpgaming.beans.value.internet.web.CashValue
import com.onepiece.gpgaming.beans.value.internet.web.PlatformMemberVo
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.core.service.PlatformMemberService
import com.onepiece.gpgaming.core.service.PromotionService
import com.onepiece.gpgaming.core.service.TransferOrderService
import com.onepiece.gpgaming.core.service.WalletService
import com.onepiece.gpgaming.core.utils.OrderIdBuilder
import com.onepiece.gpgaming.games.GameApi
import com.onepiece.gpgaming.games.GameValue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime


interface ITransferUtil {

    fun transferInAll(clientId: Int, memberId: Int, username: String, exceptPlatform: Platform? = null): List<CashValue.BalanceAllInVo>

    fun transfer(clientId: Int, username: String, cashTransferReq: CashValue.CashTransferReq, platformMemberVo: PlatformMemberVo): GameValue.TransferResp

    fun handlerPromotion(platformMember: PlatformMember, platformBalance: BigDecimal, outstanding: BigDecimal, overPromotionAmount: BigDecimal?, amount: BigDecimal, promotionId: Int?): PlatformMemberTransferUo?

    fun checkCleanPromotion(promotion: Promotion, platformMember: PlatformMember, platformBalance: BigDecimal, outstanding: BigDecimal, transferOutAmount: BigDecimal): Boolean
}


@Component
open class TransferUtil(
        private val walletService: WalletService,
        private val platformBindService: PlatformBindService,
        private val orderIdBuilder: OrderIdBuilder,
        private val transferOrderService: TransferOrderService,
        private val platformMemberService: PlatformMemberService,
        private val gameApi: GameApi,
        private val promotionService: PromotionService,
        private val memberService: MemberService
) : ITransferUtil {

    private val log = LoggerFactory.getLogger(TransferUtil::class.java)


    /**
     * ????????????????????????-1 ???????????????
     */
    override fun transferInAll(clientId: Int, memberId: Int, username: String, exceptPlatform: Platform?): List<CashValue.BalanceAllInVo> {

        val amount = BigDecimal.valueOf(-1)

        val platformMembers = this.platformMemberService.myPlatforms(memberId = memberId)
        if (platformMembers.isEmpty()) return emptyList()

        val list = kAsync(clientId = clientId, username = username, amount = amount, pms = platformMembers)

        val wallet = walletService.getMemberWallet(memberId)
        val centerBalance = CashValue.BalanceAllInVo(platform = Platform.Center, balance = wallet.balance)

        return list.plus(centerBalance)
    }

    fun kAsync(clientId: Int, username: String, amount: BigDecimal, pms: List<PlatformMemberVo>) = runBlocking {
        GlobalScope.async {
            pms.map { platformMember ->
                async {

                    val req = CashValue.CashTransferReq(from = platformMember.platform, to = Platform.Center, amount = amount, promotionId = null, code = null)
                    try {
                        val resp = singleTransfer(clientId = clientId, platform = platformMember.platform, cashTransferReq = req, type = "in",
                                platformMemberVo = platformMember, username = username)
                        val balance = if (resp.balance.toInt() <= 0) BigDecimal.ZERO else resp.balance

                        CashValue.BalanceAllInVo(platform = platformMember.platform, balance = balance)
                    } catch (e: Exception) {
//                log.error("??????????????????:", e)

                        try {
                            val balance = gameApi.balance(clientId = clientId, memberId = platformMember.memberId, platformUsername = platformMember.platformUsername,
                                    platform = platformMember.platform, platformPassword = platformMember.platformPassword)
                            CashValue.BalanceAllInVo(platform = platformMember.platform, balance = balance)
                        } catch (e1: Exception) {
                            CashValue.BalanceAllInVo(platform = platformMember.platform, balance = BigDecimal.valueOf(-1))
                        }
                    }
                }
            }.map {
                it.await()
            }
        }.await()
    }

    /**
     * ????????????????????????-1 ???????????????
     */
//    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRES_NEW)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    override fun transfer(clientId: Int, username: String, cashTransferReq: CashValue.CashTransferReq, platformMemberVo: PlatformMemberVo): GameValue.TransferResp {
        val (type, platform) = if (cashTransferReq.from == Platform.Center) "out" to cashTransferReq.to else "in" to cashTransferReq.from
        return singleTransfer(clientId = clientId, platform = platform, cashTransferReq = cashTransferReq, type = type, platformMemberVo = platformMemberVo, username = username)
    }


    private fun singleTransfer(clientId: Int, username: String, platform: Platform, cashTransferReq: CashValue.CashTransferReq, platformMemberVo: PlatformMemberVo, type: String): GameValue.TransferResp {

        val platformMember = platformMemberService.get(platformMemberVo.id)
        val (platformBalance, outstanding) = gameApi.getBalanceIncludeOutstanding(clientId = clientId, memberId = platformMemberVo.memberId, platformUsername = platformMemberVo.platformUsername,
                platform = platform, platformPassword = platformMember.password)

        return when (type) {

            // ???????????? -> ????????????
            "out" -> {
                this.centerToPlatformTransfer(platformMember = platformMember, username = username, platformBalance = platformBalance, transferAmount = cashTransferReq.amount,
                        promotionId = cashTransferReq.promotionId)
            }
            // ???????????? -> ????????????
            "in" -> {
                // ???????????????-1 ?????????????????????
                val amount = if (cashTransferReq.amount.toInt() == -1) platformBalance else cashTransferReq.amount

                // ????????????????????? ???????????????
                if (amount.toDouble() < 1) {
                    GameValue.TransferResp.successful()
                } else {
                    this.platformToCenterTransfer(platformMember = platformMember, platformBalance = platformBalance, outstanding = outstanding, amount = amount, username = username)
                }
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    // ???????????????
    private fun centerToPlatformTransfer(username: String, platformMember: PlatformMember, platformBalance: BigDecimal, transferAmount: BigDecimal, promotionId: Int?): GameValue.TransferResp {

        val platform = platformMember.platform
        val from = Platform.Center
        val to = platformMember.platform
        val clientId = platformMember.clientId
        val memberId = platformMember.memberId

        // ????????????
        val wallet = walletService.getMemberWallet(platformMember.memberId)
        val amount = if (transferAmount.toInt() == -1) wallet.balance else transferAmount
        if (amount.toDouble() < 1) return GameValue.TransferResp.successful() // ????????????????????????0 ??????
//        check(wallet.balance.toDouble() - amount.toDouble() > 0) { OnePieceExceptionCode.BALANCE_SHORT_FAIL }

        // ????????????????????????
        val platformMemberTransferUo = this.handlerPromotion(platformMember = platformMember, amount = amount, promotionId = promotionId,
                platformBalance = platformBalance, overPromotionAmount = null, outstanding = BigDecimal.ZERO)
//        check(platformMemberTransferUo?.joinPlatform == null || platformMember.platform == platformMemberTransferUo.joinPlatform) { OnePieceExceptionCode.ILLEGAL_OPERATION }

        // ??????????????????????????????
        if (platformMemberTransferUo?.category == PromotionCategory.First) {
            val member = memberService.getMember(memberId)
            check(!member.firstPromotion) { OnePieceExceptionCode.AUTHORITY_FAIL }
        }

        // ????????????????????? ??????????????????????????????
        val promotion = if (promotionId != null && promotionId != -100) {
            val promotion = promotionService.get(promotionId)

            if (promotion.category == PromotionCategory.First) {
                log.info("??????????????????????????????????????????")
                val memberUo = MemberUo(id = platformMember.memberId, firstPromotion = true)
                memberService.update(memberUo)
            }

            promotion
        } else null

        // ???????????????????????????
        platformBindService.updateEarnestBalance(clientId = clientId, platform = platform, earnestBalance = amount.negate())

        // ??????????????????
        val transferOrderId = orderIdBuilder.generatorTransferOrderId(clientId = clientId, platform = platform, transfer = "out", platformUsername = platformMember.username)

        // ??????????????????
        val event = if (promotion?.category == PromotionCategory.Introduce) WalletEvent.INTRODUCE else WalletEvent.TRANSFER_OUT
        val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = event, money = amount,
                remarks = "Center => $platform", waiterId = null, eventId = transferOrderId, giftBalance = platformMemberTransferUo?.promotionAmount)
        walletService.update(walletUo)

        // ??????????????????
        val promotionAmount = platformMemberTransferUo?.promotionAmount ?: BigDecimal.ZERO
        val transferOrderCo = TransferOrderCo(orderId = transferOrderId, clientId = clientId, memberId = memberId, money = amount, promotionAmount = promotionAmount,
                from = from, to = to, joinPromotionId = platformMemberTransferUo?.joinPromotionId, promotionJson = platformMemberTransferUo?.promotionJson, username = username,
                requirementBet = platformMemberTransferUo?.requirementBet ?: BigDecimal.ZERO, promotionPreMoney = platformMemberTransferUo?.promotionPreMoney ?: amount)
        transferOrderService.create(transferOrderCo)

        // ????????????????????????
        if (platformMemberTransferUo != null) {
            platformMemberService.transferIn(platformMemberTransferUo)
        } else {
            val init = PlatformMemberTransferUo(id = platformMember.id, joinPromotionId = null, currentBet = BigDecimal.ZERO, requirementBet = BigDecimal.ZERO,
                    promotionAmount = BigDecimal.ZERO, transferAmount = amount, requirementTransferOutAmount = BigDecimal.ZERO, ignoreTransferOutAmount = BigDecimal.ZERO,
                    promotionJson = null, platforms = emptyList(), category = PromotionCategory.First, promotionPreMoney = amount)
            platformMemberService.transferIn(init)
        }

        //????????????????????????
        val transferResp = gameApi.transfer(clientId = clientId, memberId = memberId, platformUsername = platformMember.username,
                orderId = transferOrderId, amount = amount.plus(promotionAmount), platform = to, originBalance = platformBalance,
                platformPassword = platformMember.password)

        // ???????????? ????????????
        if (!transferResp.transfer) {
            this.transferRollBack(clientId = clientId, memberId = memberId, money = amount, from = from, to = to, transferOrderId = transferOrderId)
        }


        // ??????????????????
        try {
            val state = if (transferResp.transfer) TransferState.Successful else TransferState.Fail
            val transferOrderUo = TransferOrderUo(orderId = transferOrderId, state = state, transferOutAmount = null)
            transferOrderService.update(transferOrderUo)
        } catch (e: Exception) {
            log.error("????????????????????? Center => ${platformMember.platform}, ??????: username, ??????Id???$transferOrderId")
        }

        return transferResp
    }

    private fun transferRollBack(clientId: Int, memberId: Int, money: BigDecimal, from: Platform, to: Platform, transferOrderId: String) {

        val event = if (from == Platform.Center) WalletEvent.TRANSFER_OUT_ROLLBACK else WalletEvent.TRANSFER_IN_ROLLBACK
        val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = event, money = money,
                remarks = "$from => $to", waiterId = null, eventId = transferOrderId)
        walletService.update(walletUo)

    }

    /**
     * ??????????????????
     */
    override fun handlerPromotion(platformMember: PlatformMember, platformBalance: BigDecimal, outstanding: BigDecimal, overPromotionAmount: BigDecimal?, amount: BigDecimal, promotionId: Int?): PlatformMemberTransferUo? {

        log.info("??????????????????,????????????Id")

        // ???????????????????????????
        if (platformMember.joinPromotionId != null) {
            val promotion = promotionService.get(platformMember.joinPromotionId!!)
            // ??????????????????????????????
            val cleanState = this.checkCleanPromotion(promotion = promotion, platformBalance = platformBalance,  outstanding = outstanding, platformMember = platformMember, transferOutAmount = BigDecimal.ZERO)
            check(cleanState) { OnePieceExceptionCode.PLATFORM_HAS_BALANCE_PROMOTION_FAIL }
        }

        if (promotionId == null) return null

        // ??????????????????????????????
        val promotion = promotionService.get(promotionId)
        check(promotion.status == Status.Normal) { OnePieceExceptionCode.PROMOTION_EXPIRED }
        check(this.checkStopTime(promotion.stopTime)) { OnePieceExceptionCode.PROMOTION_EXPIRED }

        // ??????????????????
        val overPromotionAmountNotNull = if (overPromotionAmount == null) {
            val history = transferOrderService.queryLastPromotion(clientId = platformMember.clientId, memberId = platformMember.memberId, startTime = LocalDateTime.now())
            PromotionPeriod.getOverPromotionAmount(promotion = promotion, historyOrders = history)
        } else overPromotionAmount

        val transferUo = promotion.getPlatformMemberTransferUo(platformMemberId = platformMember.id, amount = amount,
                platformBalance = platformBalance, promotionId = promotion.id, overPromotionAmount = overPromotionAmountNotNull)

        // ????????????????????????????????????????????????
        check(transferUo.platforms.contains(platformMember.platform)) { OnePieceExceptionCode.ILLEGAL_OPERATION }
        return transferUo
    }

    /**
     * ????????????????????????????????????
     */
    override fun checkCleanPromotion(promotion: Promotion, platformMember: PlatformMember, platformBalance: BigDecimal, outstanding: BigDecimal, transferOutAmount: BigDecimal): Boolean {

        val flag = when {
            platformBalance.plus(outstanding).toDouble() <= promotion.rule.ignoreTransferOutAmount.toDouble() -> {
                log.info("??????:${platformMember.memberId}, ???????????????????????????????????????:$platformBalance, ??????????????????${outstanding}, ?????????????????????${promotion.rule.ignoreTransferOutAmount}")
                true
            }
            promotion.ruleType == PromotionRuleType.Bet -> {
                log.info("??????:${platformMember.memberId}, ??????????????????????????????????????????:${platformMember.currentBet}, ???????????????${platformMember.requirementBet}")
                platformMember.currentBet.toDouble() >= platformMember.requirementBet.toDouble()
            }
            promotion.ruleType == PromotionRuleType.Withdraw -> {
                log.info("??????:${platformMember.memberId}, ???????????????????????????????????????:$platformBalance, ???????????????${platformMember.requirementTransferOutAmount}")
                platformBalance.toDouble() >= platformMember.requirementTransferOutAmount.toDouble()
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }


        if (flag) {
            //TODO ??????clean?????? ????????????????????????????????????

            // ????????????????????????????????????
            transferOrderService.logPromotionEnd(clientId = promotion.clientId, memberId = platformMember.memberId, promotionId = promotion.id, transferOutAmount = transferOutAmount)

            // ??????????????????????????????
            platformMemberService.cleanTransferIn(memberId = platformMember.memberId, platform = platformMember.platform)
        }

        return flag
    }


    private fun checkStopTime(stopTime: LocalDateTime?): Boolean {
        return stopTime?.isAfter(LocalDateTime.now()) ?: true
    }


    private fun platformToCenterTransfer(platformMember: PlatformMember, username: String, platformBalance: BigDecimal, outstanding: BigDecimal, amount: BigDecimal): GameValue.TransferResp {
        val platform = platformMember.platform
        val from = platformMember.platform
        val to = Platform.Center
        val clientId = platformMember.clientId
        val memberId = platformMember.memberId


        // ??????????????????????????????
        if (platformMember.joinPromotionId != null) {
            val promotion = promotionService.get(platformMember.joinPromotionId!!)
            val checkState = this.checkCleanPromotion(promotion = promotion, platformMember = platformMember, platformBalance = platformBalance, transferOutAmount = amount, outstanding = outstanding)
            check(checkState) { OnePieceExceptionCode.PLATFORM_TO_CENTER_FAIL }
        }


        // ???????????????????????????
        platformBindService.updateEarnestBalance(clientId = clientId, platform = from, earnestBalance = amount)


        // ??????????????????
        val transferOrderId = orderIdBuilder.generatorTransferOrderId(clientId = clientId, platform = platform, transfer = "in", platformUsername = platformMember.username)
        val transferOrderCo = TransferOrderCo(orderId = transferOrderId, clientId = clientId, memberId = memberId, money = amount, promotionAmount = BigDecimal.ZERO,
                from = from, to = to, joinPromotionId = null, promotionJson = null, username = username, requirementBet = BigDecimal.ZERO, promotionPreMoney = amount)
        transferOrderService.create(transferOrderCo)


        //????????????????????????
        val transferResp = gameApi.transfer(clientId = clientId, memberId = memberId, platformUsername = platformMember.username,
                orderId = transferOrderId, amount = amount.negate(), platform = platform, originBalance = platformBalance, platformPassword = platformMember.password)

        // ??????????????????
        val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = WalletEvent.TRANSFER_IN, money = amount,
                remarks = "$platform => Center", waiterId = null, eventId = transferOrderId)
        walletService.update(walletUo)

        if (!transferResp.transfer) {
            this.transferRollBack(clientId = clientId, memberId = memberId, money = amount, from = from, to = to, transferOrderId = transferOrderId)
        }

        // ??????????????????
        try {
            val transferState = if (transferResp.transfer) TransferState.Successful else TransferState.Fail
            val transferOrderUo = TransferOrderUo(orderId = transferOrderId, state = transferState, transferOutAmount = null)
            transferOrderService.update(transferOrderUo)

            // ??????????????????????????????
            if (transferResp.transfer)
                platformMemberService.cleanTransferIn(memberId = memberId, platform = platform, transferOutAmount = amount)
        } catch (e: Exception) {
            log.error("?????????????????????${platformMember.platform} => Center, ??????: username, ??????Id???$transferOrderId")
        }

        return transferResp
    }


}