package com.onepiece.treasure.games.sport

import com.onepiece.treasure.beans.model.token.LbcClientToken
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import org.springframework.stereotype.Service
import java.math.BigDecimal

//@Service
class LbcService : PlatformService() {

    private val LBC_START_URL = "http://c.gsoft888.net/Deposit_ProcessLogin.aspx?lang=en&OType=1&WebSkinType=3&skincolor=bl001&g="
    private val LBC_START_MOBILE_URL = "http://i.gsoft888.net/Deposit_ProcessLogin.aspx?lang=en&OType=1&skincolor=bl001&ischinaview=True&st="

    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as LbcClientToken

        /**
         * 1 马来盘 2 香港盘 3 欧洲盘 4 印尼盘 5 美国盘
         */
        val urlParam = listOf(
                "OpCode=${clientToken.opCode}",
                "PlayerName=${registerReq.username}",
                "OddsType=1",
                "MaxTransfer=100000",
                "MinTransfer=1"
        )

        // SecurityToken

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as LbcClientToken

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {
        val clientToken = transferReq.token as LbcClientToken

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}