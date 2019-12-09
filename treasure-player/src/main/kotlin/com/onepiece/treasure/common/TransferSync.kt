package com.onepiece.treasure.common

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.value.internet.web.PlatformMemberVo
import com.onepiece.treasure.controller.TransferUtil
import com.onepiece.treasure.controller.value.CashTransferReq
import com.onepiece.treasure.jwt.JwtUser
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.math.BigDecimal

interface TransferSync {

    fun asyncTransfer(current: JwtUser, platformMemberVo: PlatformMemberVo)

}

@Component
open class TransferSyncImpl(
        private val transferUtil: TransferUtil
): TransferSync {

    /**
     * 异步转账
     */
    @Async
    override fun asyncTransfer(current: JwtUser, platformMemberVo: PlatformMemberVo) {

        val platform = platformMemberVo.platform

        // 从其它钱包转到中心钱包
        transferUtil.transferInAll(clientId = current.clientId, memberId = current.id, exceptPlatform = platform)

        // 从中心钱包转到
        val cashTransferReq = CashTransferReq(from = Platform.Center, to = platform, amount = BigDecimal.valueOf(-1), promotionId = null)
        transferUtil.transfer(clientId = current.clientId, platformMemberVo = platformMemberVo, cashTransferReq = cashTransferReq)
    }

}