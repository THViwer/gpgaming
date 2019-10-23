package com.onepiece.treasure.web.controller.value

import com.onepiece.treasure.core.model.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

object ClientBankValueFactory {

    fun generatorClientBanks(): List<ClientBankVo> {

        val now = LocalDateTime.now()

        val b1 = ClientBankVo(id = 1, bankId = 1, bankName = "工商银行", name = "张三", bankCardNumber = "62222222222", status = Status.Normal, createdTime = now)
        val b2 = b1.copy(id = 2, bankId = 1, bankName = "工商银行", name = "李四", bankCardNumber = "633333333")
        val b3 = b1.copy(id = 3, bankId = 2, bankName = "建设银行", name = "王五", bankCardNumber = "644444444")

        return listOf(b1, b2, b3)
    }

}

data class ClientBankVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("银行Id")
        val bankId: Int,

        @ApiModelProperty("银行名称")
        val bankName: String,

        @ApiModelProperty("姓名")
        val name: String,

        @ApiModelProperty("银行卡号")
        val bankCardNumber: String,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime

)

data class ClientBankCo(

        @ApiModelProperty("银行Id")
        val bankId: Int,

        @ApiModelProperty("姓名")
        val name: String,

        @ApiModelProperty("银行卡号")
        val bankCardNumber: String,

        @ApiModelProperty("状态")
        val status: Status

)

data class ClientBankUo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("银行Id")
        val bankId: Int,

        @ApiModelProperty("姓名")
        val name: String,

        @ApiModelProperty("银行卡号")
        val bankCardNumber: String,

        @ApiModelProperty("状态")
        val status: Status

)