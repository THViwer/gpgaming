package com.onepiece.treasure.games.live.sexy

import java.math.BigDecimal

sealed class SexyValue {

    data class Login(
            val status: Int,

            val key: String?,

            val url: String?
    )

    data class Balance(
            val status: Int,

            val count: Int,

            val querytime: String,

            val results: List<BalanceData>

    ) {
        data class BalanceData(
                val user: String,

                val balance: BigDecimal
        )
    }

    data class Deposit(
            val status: Int
    )

    data class Withdraw(
            val status: Int,

            val currency: Int,

            val withdrawBalance: BigDecimal,

            val remainingBalance: BigDecimal
    )

    data class BetOrder(
            val status: Int,

            val totalBetAmount: BigDecimal,

            val totalWinAmount: BigDecimal,

            val totalWinLoss: BigDecimal,

            val totalLossAmount: BigDecimal,

            val totalTxnAmount: BigDecimal,

            val totalValidBet: BigDecimal,

            val dateTime: String,

            val data: List<BetOrderData>

    ) {

        data class BetOrderData(

                val userId:  String,

                val roundId: String,

                val gameTYpe: String,

                val betAmount: BigDecimal,

                val winAmount: BigDecimal,

                val winLoss: BigDecimal,

                val lossAmount: BigDecimal,

                val txnAmount: BigDecimal,

                val validBet: BigDecimal
        )
    }


}