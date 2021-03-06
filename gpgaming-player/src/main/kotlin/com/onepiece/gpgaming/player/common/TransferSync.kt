package com.onepiece.gpgaming.player.common

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.value.internet.web.CashValue
import com.onepiece.gpgaming.beans.value.internet.web.PlatformMemberVo
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.player.controller.TransferUtil
import com.onepiece.gpgaming.player.jwt.JwtUser
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.math.BigDecimal

interface TransferSync {

    fun asyncTransfer(current: JwtUser, platformMemberVo: PlatformMemberVo)

}

@Component
open class TransferSyncImpl(
        private val transferUtil: TransferUtil,
        private val memberService: MemberService
): TransferSync {

    /**
     * TODO 暂时关闭
     * 异步转账
     */
    @Async
    override fun asyncTransfer(current: JwtUser, platformMemberVo: PlatformMemberVo) {

        val platform = platformMemberVo.platform
        if (platform == Platform.Kiss918 || platform == Platform.Pussy888 || platform == Platform.Mega) {
            return
        }

        // 查看自动转账配置
        val member = memberService.getMember(current.id)
        if (!member.autoTransfer) return

        // 从其它钱包转到中心钱包
        transferUtil.transferInAll(clientId = current.clientId, memberId = current.id, exceptPlatform = platform, username = current.username.split("@")[1])

        // 从中心钱包转到
        val cashTransferReq = CashValue.CashTransferReq(from = Platform.Center, to = platform, amount = BigDecimal.valueOf(-1), promotionId = null, code = null)
        transferUtil.transfer(clientId = current.clientId, platformMemberVo = platformMemberVo, cashTransferReq = cashTransferReq, username = current.username)
    }

}