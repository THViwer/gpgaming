package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.DepositState
import com.onepiece.gpgaming.beans.enums.WithdrawState
import io.swagger.annotations.ApiModelProperty
import springfox.documentation.annotations.ApiIgnore
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

object WithdrawValueFactory {

//    fun generatorWithdrawVos(): List<WithdrawVo> {
//
//        val now = LocalDateTime.now()
//
//        val t1 = WithdrawVo(orderId = UUID.randomUUID().toString(), money = BigDecimal(100), bankId = 1, bankName = "工商银行", bankCardNumber = "6222222", memberId = 1,
//                name = "张三", state = WithdrawState.Process, createdTime = now, successfulTime = null, bankOrderId = null, remark = null)
//
//        val t2 = t1.copy(orderId = UUID.randomUUID().toString(), money = BigDecimal(200), name = "李四", bankCardNumber = "6333333",
//                state = WithdrawState.Successful, successfulTime = now, memberId = 2)
//
//        val t3 = t1.copy(orderId = UUID.randomUUID().toString(), money = BigDecimal(300), name = "王五", bankCardNumber = "6444444",
//                state = WithdrawState.Fail, remark = "信息错误", memberId = 3)
//
//
//        return listOf(t1, t2, t3)
//    }

}

data class WithdrawVo(

        @ApiModelProperty("订单Id")
        val orderId: String,

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("操作金额")
        val money: BigDecimal,

        @ApiModelProperty("银行Id")
        val memberBankId: Int,

        @ApiModelProperty("银行")
        val memberBank: Bank,

        @ApiModelProperty("银行卡号")
        val memberBankCardNumber: String,

        @ApiModelProperty("会员Id")
        val memberId: Int,

        @ApiModelProperty("取款人姓名")
        val memberName: String,

        @ApiModelProperty("锁定人员客服Id")
        val lockWaiterId: Int?,

        @ApiModelProperty("状态")
        val state: WithdrawState,

        @ApiModelProperty("备注")
        val remark: String?,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime,

        @ApiModelProperty("操作时间")
        val endTime: LocalDateTime?

)

data class WithdrawUoReq(

        @ApiModelProperty("订单Id")
        val orderId: String,

        @ApiModelProperty("订单状态")
        val state: WithdrawState,

        @ApiModelProperty("备注")
        val remarks: String?,

        @ApiModelProperty(hidden = true)
        val clientId: Int = 0,

        @ApiModelProperty(hidden = true)
        val waiterId: Int = 0

)
