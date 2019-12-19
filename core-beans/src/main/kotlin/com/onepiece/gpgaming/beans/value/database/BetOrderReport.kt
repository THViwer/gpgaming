package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Platform
import java.math.BigDecimal

data class BetOrderReport(

        // 厅主Id
        val clientId: Int,

        // 平台
        val platform: Platform,

        // 总下注
        val totalBet: BigDecimal,

        // 玩家总盈利
        val totalWin: BigDecimal
)