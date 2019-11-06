package com.onepiece.treasure.games.kiss918

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDate

sealed class Kiss918Value {

    data class TransferResult(

            val acc: String,

            val code: Int,

            val money: BigDecimal,

            val msg: String,

            val success: Boolean
    )

    data class Userinfo(
            val account: String,

            val agent: String,

            val disOnlineDay: Int,

            val gameId: String,

            val lobbyIdx: Int,

            val moneyNumber: BigDecimal,

            val name: String,

            val redme: String,

            val rownum: Int,

            val socreNum: BigDecimal,

            val status: Int,

            val tableID: Int,

            val tel: String,

            val type: Int,

            val userAreaID: Int,

            val idx: Int,

            val pwd: String,

            val success: Boolean
    )

    data class ReportResult(

            val viewType: Int,

            val msg: String,

            val success: Boolean,

            val result: List<Report>

    )

    data class Report(

            @JsonFormat(pattern = "yyyy-MM-dd")
            val myDate: LocalDate,

            val win: BigDecimal
    )


}