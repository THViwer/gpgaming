//package com.onepiece.treasure.games.slot.pussy888
//
//import com.fasterxml.jackson.annotation.JsonFormat
//import com.fasterxml.jackson.annotation.JsonProperty
//import java.math.BigDecimal
//import java.time.LocalDate
//
//sealed class Pussy888Value {
//
//    data class RegisterUsernameResult(
//
//            val account: String,
//
//            val code: Int,
//
//            val msg: String,
//
//            val success: Boolean
//
//    )
//
//    data class TransferResult(
//
//            val acc: String,
//
//            val code: Int,
//
//            val money: BigDecimal,
//
//            val msg: String,
//
//            val success: Boolean
//    )
//
//    data class Userinfo(
//
//            @JsonProperty("Account")
//            val account: String,
//
//            @JsonProperty("Agent")
//            val agent: String,
//
//            @JsonProperty("DisOnlineDay")
//            val disOnlineDay: Int,
//
//            @JsonProperty("GameID")
//            val gameId: String,
//
//            @JsonProperty("LobbyIdx")
//            val lobbyIdx: Int,
//
//            @JsonProperty("MoneyNum")
//            val moneyNumber: BigDecimal,
//
//            @JsonProperty("Name")
//            val name: String,
//
//            @JsonProperty("Redme")
//            val redme: String,
//
//            @JsonProperty("Rownum")
//            val rownum: Int,
//
//            @JsonProperty("ScoreNum")
//            val socreNum: BigDecimal,
//
//            @JsonProperty("Status")
//            val status: Int,
//
//            @JsonProperty("TableID")
//            val tableID: Int,
//
//            @JsonProperty("Tel")
//            val tel: String,
//
//            @JsonProperty("Type")
//            val type: Int,
//
//            @JsonProperty("UserAreaID")
//            val userAreaID: Int,
//
//            val idx: Int,
//
//            val success: Boolean
//    )
//
//    data class ReportResult(
//
//            val viewType: Int,
//
//            val msg: String,
//
//            val success: Boolean,
//
//            val results: List<Report>
//
//    )
//
//    data class Report(
//
//            val type: Int,
//
//            @JsonProperty("mydate")
//            @JsonFormat(pattern = "yyyy-MM-dd")
//            val myDate: LocalDate,
//
//            val win: BigDecimal,
//
//            val selfwin: BigDecimal,
//
//            val pump: Int,
//
//            val yield: Int,
//
//            val press: BigDecimal,
//
//            val agentwin: BigDecimal,
//
//            val jtime: Long
//
//    )
//}