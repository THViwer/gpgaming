package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.DepositState
import com.onepiece.gpgaming.beans.enums.WithdrawState
import io.swagger.annotations.ApiModelProperty
import springfox.documentation.annotations.ApiIgnore
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

sealed class WithdrawValue {

        data class WithdrawTotal(

                @ApiModelProperty("列表数据")
                val data: List<WithdrawVo>

        ) {

                val totalSuccessMoney: BigDecimal
                        @ApiModelProperty("总成功金额")
                        get() {
                                return data.filter { it.state == WithdrawState.Successful }.sumByDouble { it.money.toDouble() }.toBigDecimal().setScale(2, 2)
                        }

                val totalSuccessCount: Int
                        @ApiModelProperty("总成功次数")
                        get() {
                                return data.filter { it.state == WithdrawState.Successful }.count()
                        }

                val totalFailMoney: BigDecimal
                        @ApiModelProperty("总失败金额")
                        get() {
                                return data.filter { it.state == WithdrawState.Fail }.sumByDouble { it.money.toDouble() }.toBigDecimal().setScale(2, 2)
                        }

                val totalFailCount: Int
                        @ApiModelProperty("总失败次数")
                        get() {
                                return data.filter { it.state == WithdrawState.Fail }.count()
                        }


        }


        data class WithdrawVo(

                val id: Int,

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

                @ApiModelProperty("锁定人员用户名")
                val lockWaiterUsername: String?,

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
}


