package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.time.LocalDateTime

object ClientBankValueFactory {
//
//    fun generatorClientBanks(): List<ClientBankVo> {
//
//        val now = LocalDateTime.now()
//
//        val b1 = ClientBankVo(id = 1, bankId = 1, bankName = "工商银行", name = "张三", bankCardNumber = "62222222222", status = Status.Normal, createdTime = now)
//        val b2 = b1.copy(id = 2, bankId = 1, bankName = "工商银行", name = "李四", bankCardNumber = "633333333")
//        val b3 = b1.copy(id = 3, bankId = 2, bankName = "建设银行", name = "王五", bankCardNumber = "644444444")
//
//        return listOf(b1, b2, b3)
//    }

}

data class BankVo(

        val bank: Bank,

        val country: Country,

        val name: String,

        val grayLogo: String,

        val logo: String

)

data class ClientBankVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("银行")
        val bank: Bank,

        @ApiModelProperty("银行名称")
        val bankName: String,

        @ApiModelProperty("银行的logo 灰色")
        val grayLogo: String,

        @ApiModelProperty("银行的logo")
        val logo: String,

        @ApiModelProperty("姓名")
        val name: String,

        @ApiModelProperty("银行卡号")
        val bankCardNumber: String,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("层级Id")
        val levelId: Int?,

        @ApiModelProperty("层级名称")
        val levelName: String?,

        @ApiModelProperty("最小充值金额")
        val minAmount: BigDecimal,

        @ApiModelProperty("最大充值金额")
        val maxAmount: BigDecimal,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime

)

data class ClientBankCoReq(

        @ApiModelProperty("层级Id")
        val levelId: Int?,

        @ApiModelProperty("银行信息")
        val bank: Bank,

        @ApiModelProperty("姓名")
        val name: String,

        @ApiModelProperty("银行卡号")
        val bankCardNumber: String,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("最小充值金额")
        val minAmount: BigDecimal,

        @ApiModelProperty("最大充值金额")
        val maxAmount: BigDecimal

)

data class ClientBankUoReq(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("银行")
        val bank: Bank?,

        @ApiModelProperty("层级Id")
        val levelId: Int?,

        @ApiModelProperty("姓名")
        val name: String?,

        @ApiModelProperty("银行卡号")
        val bankCardNumber: String?,

        @ApiModelProperty("状态")
        val status: Status?,

        @ApiModelProperty("最小充值金额")
        val minAmount: BigDecimal?,

        @ApiModelProperty("最大充值金额")
        val maxAmount: BigDecimal?

)