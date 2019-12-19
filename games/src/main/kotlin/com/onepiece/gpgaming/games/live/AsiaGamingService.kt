package com.onepiece.gpgaming.games.live

import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class AsiaGamingService : PlatformService() {

    override fun register(registerReq: GameValue.RegisterReq): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}