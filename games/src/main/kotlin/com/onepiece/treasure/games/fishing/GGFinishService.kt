package com.onepiece.treasure.games.fishing

import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class GGFinishService : PlatformService() {


    override fun register(registerReq: GameValue.RegisterReq): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}