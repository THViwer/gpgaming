package com.onepiece.treasure.games.live.golddeluxe

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.math.BigDecimal
import java.util.HashMap
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue


sealed class GoldDeluxeValue {

    data class Header(

            @JacksonXmlProperty(localName = "Method")
            val method: String = "",

            @JacksonXmlProperty(localName = "ErrorCode")
            val errorCode: String = "",

            @JacksonXmlProperty(localName = "MerchantID")
            val merchantID: String = "",

            @JacksonXmlProperty(localName = "MessageID")
            val messageID: String = ""

    )

    @JacksonXmlRootElement(localName = "Reply")
    data class RegisterResult(

            @JacksonXmlProperty(localName = "Header")
            val header: Header,

            @JacksonXmlProperty(localName = "Param")
            val param: Param

    ) {

        data class Param(
                @JacksonXmlProperty(localName = "UserID")
                val userId: String = "",

                @JacksonXmlProperty(localName = "CurrencyCode")
                val currencyCode: String = "",

                @JacksonXmlProperty(localName = "ErrorDesc")
                val errorDesc: String = ""
        )
    }

    @JacksonXmlRootElement(localName = "Reply")
    data class BalanceResult(

            @JacksonXmlProperty(localName = "Header")
            val header: Header,

            @JacksonXmlProperty(localName = "Param")
            val param: Param
    ) {


        data class Param(

                @JacksonXmlProperty(localName = "UserID")
                val userId: String = "",

                @JacksonXmlProperty(localName = "CurrencyCode")
                val currencyCode: String = "",

                @JacksonXmlProperty(localName = "Balance")
                val balance: BigDecimal = BigDecimal.ZERO,

                @JacksonXmlProperty(localName = "PlayerGroup")
                val playerGroup: String = ""
        )
    }

    @JacksonXmlRootElement(localName = "Reply")
    data class TransferResult(
            @JacksonXmlProperty(localName = "Header")
            val header: Header,

            @JacksonXmlProperty(localName = "Param")
            val param: Param
    ) {

        data class Param(
                @JacksonXmlProperty(localName = "TransactionID")
                val transactionId: String = "",

                @JacksonXmlProperty(localName = "PaymentID")
                val paymentId: String = "",

                @JacksonXmlProperty(localName = "ErrorDesc")
                val errorDesc: String = ""

        )

    }

    @JacksonXmlRootElement(localName = "Reply")
    data class CheckTransferResult(
            @JacksonXmlProperty(localName = "Header")
            val header: Header,

            @JacksonXmlProperty(localName = "Param")
            val param: Param
    ) {

        data class Param(
                @JacksonXmlProperty(localName = "TransactionID")
                val transactionId: String = "",

                @JacksonXmlProperty(localName = "MessageID")
                val messageId: String = "",

                @JacksonXmlProperty(localName = "PaymentID")
                val paymentId: String = "",

                @JacksonXmlProperty(localName = "Status")
                val status: String = "",

                @JacksonXmlProperty(localName = "ErrorDesc")
                val errorDesc: String = ""

        )
    }

    @JacksonXmlRootElement(localName = "Reply")
    data class BetResult(

            @JacksonXmlProperty(localName = "Header")
            val header: Header,

            @JacksonXmlProperty(localName = "Param")
            val param: Param
    ) {

        data class Param(

                @JacksonXmlProperty(localName = "TotalRecord")
                val totalRecord: Int,

                @JacksonXmlProperty(localName = "ErrorDesc")
                val errorDesc: String = "",

                @JacksonXmlProperty(localName = "BetInfo")
                val betInfo: BetInfo
        )

        /**
         *       <No></No>
        <UserID></UserID>
        <Currency></Currency>
        <BetTime></<BetTime>
        <BalanceTime></<BalanceTime>
        <ProductID></ProductID>
        <ClientType></ClientType >
        <GameInterface></GameInterface>
         */
        data class BetInfo(
                val no: Int,

                val UserID: String,

                val Currency: String,

                val BetTime: String,

                val BalanceTime: String,

                val ProductID: String,

                val ClientType: String,

                val GameInterface: String

        )



    }

}
