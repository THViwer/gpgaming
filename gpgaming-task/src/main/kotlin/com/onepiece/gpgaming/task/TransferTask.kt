package com.onepiece.gpgaming.task

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.TransferState
import com.onepiece.gpgaming.beans.enums.WalletEvent
import com.onepiece.gpgaming.beans.model.TransferOrder
import com.onepiece.gpgaming.beans.value.database.TransferOrderUo
import com.onepiece.gpgaming.beans.value.database.WalletUo
import com.onepiece.gpgaming.core.dao.TransferOrderDao
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.core.service.PlatformMemberService
import com.onepiece.gpgaming.core.service.TransferOrderService
import com.onepiece.gpgaming.core.service.WalletService
import com.onepiece.gpgaming.games.GameApi
import com.onepiece.gpgaming.games.GameValue
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class TransferTask(
        private val transferOrderService: TransferOrderService,
        private val transferOrderDao: TransferOrderDao,
        private val gameApi: GameApi,
        private val walletService: WalletService,
        private val platformMemberService: PlatformMemberService,
        private val platformBindService: PlatformBindService
) {

    private val log = LoggerFactory.getLogger(TransferTask::class.java)

//    @Scheduled(cron = "0 0/1 *  * * ? ")
    @Scheduled(cron = "0/10 * *  * * ? ")
    fun start() {

        val endDate = LocalDateTime.now().minusHours(1)
        val startDate = endDate.minusDays(7)
        val orders = transferOrderDao.queryProcessOrder(startDate = startDate, endDate = endDate)
        if (orders.isEmpty()) return

        orders.forEach { order ->
            log.info("处理转账单异常情况 ：${order.orderId}")

            try {
                this.handlerProcessOrder(order = order)
            } catch (e: Exception) {
                log.info("处理转账单异常情况 ：${order.orderId}, 订单处理失败！", e)
            }

        }
    }

    private fun handlerProcessOrder(order: TransferOrder) {
        val clientId = order.clientId
        val memberId = order.memberId
        val amount = order.money
        val platform = order.from
        val from = order.from
        val to = order.to
        val transferOrderId = order.orderId

        val platformMember = platformMemberService.findPlatformMember(memberId = memberId)
                .first { it.platform == platform }

        //调用平台接口取款
        val bind = platformBindService.find(clientId = clientId, platform = platform)


        val checkTransferReq = GameValue.CheckTransferReq(orderId = order.orderId, amount = amount, platformOrderId = order.orderId, token = bind.clientToken,
                type = "withdraw", username = platformMember.username)
        val transferResp = gameApi.checkTransfer(clientId = bind.clientId, memberId = memberId, platform = platform, checkTransferReq = checkTransferReq)

        // 中心钱包加钱
        val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = WalletEvent.TRANSFER_IN, money = amount,
                remarks = "$platform => Center", waiterId = null, eventId = transferOrderId)
        walletService.update(walletUo)

        if (!transferResp.transfer) {
            this.transferRollBack(clientId = clientId, memberId = memberId, money = amount, from = from, to = to, transferOrderId = transferOrderId)
        }

        // 更新转账订单
        try {
            val transferState = if (transferResp.transfer) TransferState.Successful else TransferState.Fail
            val transferOrderUo = TransferOrderUo(orderId = transferOrderId, state = transferState, transferOutAmount = null)
            transferOrderService.update(transferOrderUo)

            // 清空平台用户优惠信息
            if (transferResp.transfer)
                platformMemberService.cleanTransferIn(memberId = memberId, platform = platform, transferOutAmount = amount)
        } catch (e: Exception) {
            log.error("可能造成死锁，${platformMember.platform} => Center, 用户: username, 订单Id：$transferOrderId")
        }
    }


    private fun transferRollBack(clientId: Int, memberId: Int, money: BigDecimal, from: Platform, to: Platform, transferOrderId: String) {

        val event = if (from == Platform.Center) WalletEvent.TRANSFER_OUT_ROLLBACK else WalletEvent.TRANSFER_IN_ROLLBACK
        val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = event, money = money,
                remarks = "$from => $to", waiterId = null, eventId = transferOrderId)
        walletService.update(walletUo)

    }

}