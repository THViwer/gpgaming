package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.DepositChannel
import com.onepiece.gpgaming.beans.enums.DepositState
import io.swagger.annotations.ApiModelProperty
import springfox.documentation.annotations.ApiIgnore
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

object DepositValueFactory {

//    fun generatorDeposits(): List<DepositVo> {
//
//        val uploadImage = "https://image.flaticon.com/sprites/new_packs/148705-essential-collection.png"
//        val now = LocalDateTime.now()
//
//        val t1 = DepositVo(orderId = UUID.randomUUID().toString(), money = BigDecimal(100), bankId = 1, bankName = "工商银行", bankCardNumber = "6222222", memberId = 1,
//                name = "张三", state = DepositState.Process, uploadImage = uploadImage, createdTime = now, successfulTime = null, bankOrderId = null, remark = null)
//        val t2 = t1.copy(orderId = UUID.randomUUID().toString(), money = BigDecimal(200), name = "李四", bankCardNumber = "6333333",
//                state = DepositState.Successful, successfulTime = now, memberId = 2)
//
//        val t3 = t1.copy(orderId = UUID.randomUUID().toString(), money = BigDecimal(300), name = "王五", bankCardNumber = "6444444",
//                state = DepositState.Fail, remark = "信息错误", memberId = 3)
//
//        val t4 = t1.copy(orderId = UUID.randomUUID().toString(), money = BigDecimal(400), name = "赵六", bankCardNumber = "6555555",
//                state = DepositState.Close, memberId = 4)
//
//        return listOf(t1, t2, t3, t4)
//    }

}

data class DepositVo(

        @ApiModelProperty("订单Id")
        val orderId: String,

        @ApiModelProperty("操作金额")
        val money: BigDecimal,

        @ApiModelProperty("存款时间")
        val depositTime: LocalDateTime,

        @ApiModelProperty("存款渠道")
        val channel: DepositChannel,

        @ApiModelProperty("银行")
        val memberBank: Bank,

        @ApiModelProperty("存款人姓名")
        val memberName: String,

        @ApiModelProperty("银行卡号")
        val memberBankCardNumber: String,

        @ApiModelProperty("厅主银行卡Id")
        val clientBankId: Int,

        @ApiModelProperty("厅主银行卡")
        val clientBank: Bank,

        @ApiModelProperty("厅主银行卡号")
        val clientBankCardNumber: String,

        @ApiModelProperty("厅主银行卡名称")
        val clientBankName: String,

        @ApiModelProperty("银行订单Id")
        val bankOrderId: String?,

        @ApiModelProperty("会员Id")
        val memberId: Int,

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("锁定人员客服Id")
        val lockWaiterId: Int?,

        @ApiModelProperty("锁定人员用户名")
        val lockWaiterUsername: String?,

        @ApiModelProperty("状态")
        val state: DepositState,

        @ApiModelProperty("备注")
        val remark: String?,

        @ApiModelProperty("上传图片地址")
        val imgPath: String?,

        @ApiModelProperty("上传时间")
        val createdTime: LocalDateTime,

        @ApiModelProperty("操作时间")
        val endTime: LocalDateTime?

)

data class DepositUoReq(

        @ApiModelProperty("订单Id")
        val orderId: String,

        @ApiModelProperty("订单状态")
        val state: DepositState,

        @ApiModelProperty("备注")
        val remarks: String?,

        @ApiModelProperty(hidden = true)
        val clientId: Int = 0,

        @ApiModelProperty(hidden = true)
        val waiterId: Int = 0

)

data class ArtificialCoReq(

        @ApiModelProperty("会员Id")
        val memberId: Int,

        @ApiModelProperty("操作金额")
        val money: BigDecimal,

        @ApiModelProperty("备注")
        val remarks: String,

        @ApiModelProperty("密码")
        val password: String
)
