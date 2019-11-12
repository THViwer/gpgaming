package com.onepiece.treasure.games.sport.lbc

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

sealed class LbcValue {

    data class RegisterResult(
            @JsonProperty("error_code")
            val errorCode: Int,

            val message: String
    )


    data class CheckBalanceRespond(
            @JsonProperty("error_code")
            val errorCode: Int,

            val message: String,

            @JsonProperty("IsCashOut")
            val isCashOut: Int,

            @JsonProperty("Data")
            val data: List<CheckBalanceContent>


    ) {

        data class CheckBalanceContent(

                val playerName: String,

                val balance: BigDecimal,

                val outstanding: Int,

                val currency: Int

        )

    }

    data class Transfer(
            @JsonProperty("error_code")
            val errorCode: Int,

            val message: String,

            @JsonProperty("Data")
            val Data: TransferData

    ) {
        data class TransferData(
                @JsonProperty("trans_id")
                val transId: Int,

                @JsonProperty("before_amount")
                val beforeAmount: BigDecimal,

                @JsonProperty("after_amount")
                val afterAmount: BigDecimal,

                val status: Int
        )
    }

    data class CheckTransfer(
            @JsonProperty("error_code")
            val errorCode: Int,

            val message: String,

            @JsonProperty("Data")
            val data: CheckTransferData

    ) {
        data class CheckTransferData(
                @JsonProperty("trans_id")
                val transId: String,

                val amount: BigDecimal,

                @JsonProperty("before_amount")
                val beforeAmount: BigDecimal,

                @JsonProperty("after_amount")
                val afterAmount: BigDecimal,

                @JsonProperty("transfer_date")
                val transferDate: String,

                val status: Int
        )
    }

    data class Login(
            @JsonProperty("error_code")
            val errorCode: Int,

            val message: String,

            val sessionToken: String

    )

}