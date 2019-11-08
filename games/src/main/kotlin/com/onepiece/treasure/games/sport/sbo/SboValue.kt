package com.onepiece.treasure.games.sport.sbo

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDateTime

sealed class SboValue {

    data class SboError(
            val id: Int,

            val msg: String
    )

    data class SboDefaultResult(
            val error: SboError,

            val serverId: String
    )

    data class SboLoginResult(

            val username: String,

            val token: String,

            val error: SboError,

            val serviceId: String
    )

    data class SboDepositResult(

            val txnId: String,

            val refno: String,

            val outstanding: BigDecimal,

            val balance: BigDecimal,

            val error: SboError,

            val serviceId: String
    )

    data class SboWithdrawResult(
            val txnId: String,

            val refno: String,

            val amount: BigDecimal,

            val balance: BigDecimal,

            val outstanding: BigDecimal,

            val error: SboError,

            val serviceId: String

    )

    data class SboCheckTransferStatusResult(
            val txnId: String,

            val refno: String,

            // deposit or withdraw
            val transactionType: String,

            val amount: BigDecimal,

            val error: SboError,

            val serviceId: String
    )

    data class SboPlayerBalanceResult(
            val username: String,

            val current: String,

            val balance: BigDecimal,

            val outstanding: BigDecimal,

            val error: SboError,

            val serviceId: String

    )

    data class CustomerReportResult(
            val playerRevenue: List<PlayerRevenue>,

            val error: SboError,

            val serviceId: String
    )

    data class PlayerRevenue(

            val username: String,

            val betcount: Count,

            val turnover: Count,

            val winlose: BigDecimal,

            val commission: BigDecimal,

            val totalDeposit: BigDecimal,

            val totalWithdraw: BigDecimal

    )

    data class Count(

        val total: BigDecimal,

        val waiting: BigDecimal,

        val running: BigDecimal,

        val lose: BigDecimal,

        val wn: BigDecimal,

        val won: BigDecimal,

        val void: BigDecimal,

        val reject: BigDecimal,

        val refund: BigDecimal

    )

    data class CustomerBetResult(

            val playerBetList: List<PlayerBet>,

            val turnover: BigDecimal,

            val error: SboError,

            val serviceId: String
    )

    data class PlayerBet(
            val username: String,

            val actualStake: BigDecimal,

            val current: String,

            val odds: BigDecimal,

            val oddsStyle: String,

            @JsonFormat(pattern = "yyyy-MM-dd HH-mm:ss")
            val orderTime: LocalDateTime,

            val refNo: String,

            val sportType: String,

            val productType: String,

            val gameId: String,

            val stake: BigDecimal,

            val status: String,

            val subBet: List<PlayerSubBet>
    )

    data class PlayerSubBet(
            val betOption: String,

            val hdp: String,

            val league: String,

            val levelScore: BigDecimal,

            val marketType: String,

            val match: String,

            val odds: BigDecimal,

            @JsonFormat(pattern = "yyyy-MM-dd HH-mm:ss")
            val winLostDate: LocalDateTime
    )


}