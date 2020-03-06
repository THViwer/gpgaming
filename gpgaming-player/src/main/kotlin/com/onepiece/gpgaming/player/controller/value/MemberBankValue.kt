package com.onepiece.gpgaming.player.controller.value

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime


data class BankVo(
        val logo: String,

        val name: String
)

data class MemberBankVo(

        val id: Int,

        @ApiModelProperty("厅主名称")
        val clientId: Int,

        @ApiModelProperty("会员Id")
        val memberId: Int,

        @ApiModelProperty("银行logo 灰色")
        val grayLogo: String,

        @ApiModelProperty("银行logo")
        val logo: String,

        @ApiModelProperty("银行")
        val bank: Bank,

        @ApiModelProperty("会员姓名")
        val name: String?,

        @ApiModelProperty("银行卡号")
        val bankCardNumber: String?

//        @ApiModelProperty("状态")
//        val status: Status,
//
//        @ApiModelProperty("创建时间")
//        val createdTime: LocalDateTime
)


data class MemberBankCoReq(

        @ApiModelProperty("银行")
        val bank: Bank,

        @ApiModelProperty("银行卡号")
        val bankCardNumber: String

)

data class MemberBankUoReq(
        val id: Int,

        @ApiModelProperty("银行")
        val bank: Bank?,

        // 会员姓名
//        val name: String,

        @ApiModelProperty("银行卡号")
        val bankCardNumber: String?,

        @ApiModelProperty("状态")
        val status: Status?

)