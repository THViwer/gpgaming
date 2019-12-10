package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.*
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Balance
import com.onepiece.treasure.beans.model.PlatformMember
import com.onepiece.treasure.beans.model.Promotion
import com.onepiece.treasure.beans.value.database.PlatformMemberTransferUo
import com.onepiece.treasure.beans.value.database.TransferOrderCo
import com.onepiece.treasure.beans.value.database.TransferOrderUo
import com.onepiece.treasure.beans.value.database.WalletUo
import com.onepiece.treasure.beans.value.internet.web.PlatformMemberVo
import com.onepiece.treasure.controller.value.BalanceAllInVo
import com.onepiece.treasure.controller.value.BalanceVo
import com.onepiece.treasure.controller.value.CashTransferReq
import com.onepiece.treasure.core.OrderIdBuilder
import com.onepiece.treasure.core.service.*
import com.onepiece.treasure.games.GameApi
import com.onepiece.treasure.games.GameValue
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.stream.Collectors

@Component
class TransferUtil(
        private val walletService: WalletService,
        private val platformBindService: PlatformBindService,
        private val orderIdBuilder: OrderIdBuilder,
        private val transferOrderService: TransferOrderService,
        private val platformMemberService: PlatformMemberService,
        private val gameApi: GameApi,
        private val promotionService: PromotionService
)  {

    private val log = LoggerFactory.getLogger(TransferUtil::class.java)


    /**
     * 如果轩心账金额为-1 则是转全部
     */
    fun transferInAll(clientId: Int, memberId: Int, exceptPlatform: Platform? = null): List<BalanceAllInVo> {

        val amount = BigDecimal.valueOf(-1)
        val platformMembers = this.platformMemberService.myPlatforms(memberId = memberId)
        val list = platformMembers.parallelStream().filter { exceptPlatform == null || exceptPlatform != it.platform }.map{ platformMember ->
            val req = CashTransferReq(from = platformMember.platform, to = Platform.Center, amount = amount, promotionId = null)
            try {
                val resp = this.singleTransfer(clientId = clientId, platform = platformMember.platform, cashTransferReq = req, type = "in", platformMemberVo = platformMember)
                val balance = if (resp.balance.toInt() <= 0) BigDecimal.ZERO else resp.balance

                BalanceAllInVo(platform = platformMember.platform, balance = balance)
            } catch (e: Exception) {
                log.error("转账平台错误:", e)
                BalanceAllInVo(platform = platformMember.platform, balance = BigDecimal.valueOf(-1))
            }
        }.collect(Collectors.toList())

        val wallet = walletService.getMemberWallet(memberId)
        val centerBalance = BalanceAllInVo(platform = Platform.Center, balance = wallet.balance)

        list.add(centerBalance)

        return list
    }

    /**
     * 如果轩心账金额为-1 则是转全部
     */
    fun transfer(clientId: Int, cashTransferReq: CashTransferReq, platformMemberVo: PlatformMemberVo): GameValue.TransferResp {
        val (type, platform) = if (cashTransferReq.from == Platform.Center) "out" to cashTransferReq.to else "in" to cashTransferReq.from
        return singleTransfer(clientId = clientId, platform =  platform, cashTransferReq = cashTransferReq, type = type, platformMemberVo = platformMemberVo)
    }


    private fun singleTransfer(clientId: Int, platform: Platform, cashTransferReq: CashTransferReq, platformMemberVo: PlatformMemberVo, type: String): GameValue.TransferResp{

        val platformMember = platformMemberService.get(platformMemberVo.id)
        val platformBalance  = gameApi.balance(clientId = clientId, platformUsername = platformMemberVo.platformUsername, platform = platform, platformPassword = platformMember.password)

        return when (type) {

            // 中心钱包 -> 平台钱包
            "out" -> {
                this.centerToPlatformTransfer(platformMember = platformMember, platformBalance = platformBalance, transferAmount = cashTransferReq.amount, promotionId = cashTransferReq.promotionId)
            }
            // 平台钱包 -> 中心钱包
            "in" -> {
                // 如果金额为-1 则转入全部金额
                val amount = if (cashTransferReq.amount.toInt() == -1) platformBalance else cashTransferReq.amount

                // 如果平台没有钱 则直接返回
                if (amount.setScale(2, 2) == BigDecimal.ZERO.setScale(2, 2)) {
                    GameValue.TransferResp.successful()
                } else {
                    this.platformToCenterTransfer(platformMember = platformMember, platformBalance = platformBalance, amount = amount)
                }
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    // 中心转平台
    private fun centerToPlatformTransfer(platformMember: PlatformMember, platformBalance: BigDecimal, transferAmount: BigDecimal, promotionId: Int?): GameValue.TransferResp {

        val platform = platformMember.platform
        val from = Platform.Center
        val to = platformMember.platform
        val clientId = platformMember.clientId
        val memberId = platformMember.memberId

        // 检查余额
        val wallet = walletService.getMemberWallet(platformMember.memberId)
        val amount = if (transferAmount.toInt() == -1) wallet.balance else transferAmount
        if (amount.toDouble() <= 0) return GameValue.TransferResp.successful() // 如果金额小于等于0 返回
//        check(wallet.balance.toDouble() - amount.toDouble() > 0) { OnePieceExceptionCode.BALANCE_SHORT_FAIL }

        // 优惠活动赠送金额
        val platformMemberTransferUo = this.handlerPromotion(platformMember = platformMember, amount = amount, promotionId = promotionId, platformBalance = platformBalance)
//        check(platformMemberTransferUo?.joinPlatform == null || platformMember.platform == platformMemberTransferUo.joinPlatform) { OnePieceExceptionCode.ILLEGAL_OPERATION }

        // 检查是否满足首次优惠
        check(wallet.totalTransferOutFrequency == 0 || platformMemberTransferUo?.category != PromotionCategory.First) { OnePieceExceptionCode.AUTHORITY_FAIL }

        // 检查保证金是否足够
        platformBindService.updateEarnestBalance(clientId = clientId, platform = platform, earnestBalance = amount.negate())

        // 转账订单编号
        val transferOrderId = orderIdBuilder.generatorTransferOrderId(clientId = clientId, platform = platform, transfer = "out", platformUsername = platformMember.username)

        // 中心钱包扣款
        val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = WalletEvent.TRANSFER_OUT, money = amount,
                remarks = "Center => $platform", waiterId = null, eventId = transferOrderId, giftBalance = platformMemberTransferUo?.promotionAmount)
        walletService.update(walletUo)

        // 生成转账订单
        val promotionAmount = platformMemberTransferUo?.promotionAmount?: BigDecimal.ZERO
        val transferOrderCo = TransferOrderCo(orderId = transferOrderId, clientId = clientId, memberId = memberId, money = amount, promotionAmount = promotionAmount,
                from = from, to = to, joinPromotionId = platformMemberTransferUo?.joinPromotionId, promotionJson = platformMemberTransferUo?.promotionJson)
        transferOrderService.create(transferOrderCo)

        // 平台钱包更改信息
        if (platformMemberTransferUo != null) {
            platformMemberService.transferIn(platformMemberTransferUo)
        } else {
            val init = PlatformMemberTransferUo(id = platformMember.id, joinPromotionId = null, currentBet = BigDecimal.ZERO, requirementBet = BigDecimal.ZERO,
                    promotionAmount = BigDecimal.ZERO, transferAmount = amount, requirementTransferOutAmount = BigDecimal.ZERO, ignoreTransferOutAmount = BigDecimal.ZERO,
                    promotionJson = null, platforms = emptyList(), category = PromotionCategory.Slot)
            platformMemberService.transferIn(init)
        }

        //调用平台接口充值
        val transferResp = gameApi.transfer(clientId = clientId, platformUsername = platformMember.username, orderId = transferOrderId, amount = amount.plus(promotionAmount),
                platform = to, originBalance = platformBalance)

        // 转账失败 进行回滚
        if (!transferResp.transfer) {
            this.transferRollBack(clientId = clientId, memberId = memberId, money = amount, from = from, to = to, transferOrderId = transferOrderId)
        }

        // 更新转账订单
        val state = if (transferResp.transfer) TransferState.Successful else TransferState.Fail
        val transferOrderUo = TransferOrderUo(orderId = transferOrderId, state = state)
        transferOrderService.update(transferOrderUo)

        return transferResp
    }

    private fun transferRollBack(clientId: Int, memberId: Int, money: BigDecimal, from: Platform, to: Platform, transferOrderId: String) {

        val event = if (from == Platform.Center) WalletEvent.TRANSFER_OUT_ROLLBACK else WalletEvent.TRANSFER_IN_ROLLBACK
        val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = event, money = money,
                remarks = "$from => $to", waiterId = null, eventId = transferOrderId)
        walletService.update(walletUo)

    }

    /**
     * 处理优惠活动
     */
    private fun handlerPromotion(platformMember: PlatformMember, platformBalance: BigDecimal, amount: BigDecimal, promotionId: Int?): PlatformMemberTransferUo? {

        // 是否有历史优惠活动
        if (platformMember.joinPromotionId != null) {
            // 已存在的优惠活动
            val promotion = promotionService.get(platformMember.joinPromotionId!!)

            // 是否满足清空优惠活动
            val cleanState = this.checkCleanPromotion(promotion = promotion, platformBalance = platformBalance, platformMember = platformMember)
            check(cleanState) { OnePieceExceptionCode.PLATFORM_HAS_BALANCE_PROMOTION_FAIL }
        }

        if (promotionId == null) return null

        // 获得新的优惠活动信息
        val promotion = promotionService.get(promotionId)
        check(promotion.status == Status.Normal) { OnePieceExceptionCode.PROMOTION_EXPIRED }
        check(this.checkStopTime(promotion.stopTime)) { OnePieceExceptionCode.PROMOTION_EXPIRED }

        val transferUo = promotion.getPlatformMemberTransferUo(platformMemberId = platformMember.id, amount =  amount, platformBalance = platformBalance, promotionId = promotion.id)

        // 检查当前平台是否是参加活动的平台
        check(transferUo.platforms.contains(platformMember.platform)) { OnePieceExceptionCode.ILLEGAL_OPERATION }
        return transferUo
    }


    /**
     * 检查是否清空优惠活动信息
     */
    fun checkCleanPromotion(promotion: Promotion, platformMember: PlatformMember, platformBalance: BigDecimal): Boolean {

        val state = when{
            platformBalance.toDouble() <= promotion.rule.ignoreTransferOutAmount.toDouble() -> true
            promotion.ruleType == PromotionRuleType.Bet -> {
                platformMember.currentBet.toDouble() >= platformMember.requirementBet.toDouble()
            }
            promotion.ruleType == PromotionRuleType.Withdraw -> {
                platformBalance.toDouble() >= platformMember.requirementTransferOutAmount.toDouble()
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }


        if (state) {
            platformMemberService.cleanTransferIn(memberId = platformMember.memberId, platform = platformMember.platform)
        }

        return state
    }


    private fun checkStopTime(stopTime: LocalDateTime?): Boolean {
        return stopTime?.isAfter(LocalDateTime.now()) ?: true
    }


    private fun platformToCenterTransfer(platformMember: PlatformMember, platformBalance: BigDecimal, amount: BigDecimal): GameValue.TransferResp {
        val platform = platformMember.platform
        val from = platformMember.platform
        val to = Platform.Center
        val clientId = platformMember.clientId
        val memberId = platformMember.memberId


        // 判断是否满足转出条件
        if (platformMember.joinPromotionId != null) {
            val promotion = promotionService.get(platformMember.joinPromotionId!!)
            val checkState = this.checkCleanPromotion(promotion = promotion, platformMember = platformMember, platformBalance = platformBalance)
            check(checkState) { OnePieceExceptionCode.PLATFORM_TO_CENTER_FAIL }
        }


        // 检查保证金是否足够
        platformBindService.updateEarnestBalance(clientId = clientId, platform = from, earnestBalance = amount)


        // 生成转账订单
        val transferOrderId = orderIdBuilder.generatorTransferOrderId(clientId = clientId, platform = platform, transfer = "in", platformUsername = platformMember.username)
        val transferOrderCo = TransferOrderCo(orderId = transferOrderId, clientId = clientId, memberId = memberId, money = amount, promotionAmount = BigDecimal.ZERO,
                from = from, to = to, joinPromotionId = null, promotionJson = null)
        transferOrderService.create(transferOrderCo)


        //调用平台接口取款
        val transferResp = gameApi.transfer(clientId = clientId, platformUsername = platformMember.username, orderId = transferOrderId, amount = amount.negate(),
                platform = platform, originBalance = platformBalance)

        // 中心钱包加钱
        val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = WalletEvent.TRANSFER_IN, money = amount,
                remarks = "$platform => Center", waiterId = null, eventId = transferOrderId)
        walletService.update(walletUo)

        if (!transferResp.transfer) {
            this.transferRollBack(clientId = clientId, memberId = memberId, money = amount, from = from, to = to, transferOrderId = transferOrderId)
        }

        // 更新转账订单
        val transferState = if (transferResp.transfer) TransferState.Successful else TransferState.Fail
        val transferOrderUo = TransferOrderUo(orderId = transferOrderId, state = transferState)
        transferOrderService.update(transferOrderUo)

        // 清空平台用户优惠信息
        if (transferResp.transfer)
            platformMemberService.cleanTransferIn(memberId = memberId, platform = platform, transferOutAmount = amount)

        return transferResp
    }


}