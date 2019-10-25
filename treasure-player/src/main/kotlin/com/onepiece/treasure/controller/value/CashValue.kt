package com.onepiece.treasure.controller.value

import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.beans.enums.WithdrawState
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

object CashValueFactory {

    fun generatorCashDepositPage(): CashDepositPage {

        val now = LocalDateTime.now()

        val t1 = CashDepositVo(orderId = UUID.randomUUID().toString(), money = BigDecimal(100), state = DepositState.Process,
                createdTime = now, successfulTime = null, remark = null)
        val t2 = t1.copy(orderId = UUID.randomUUID().toString(), money = BigDecimal(200), state = DepositState.Successful,
                createdTime = now, successfulTime = null)
        val t3 = t1.copy(orderId = UUID.randomUUID().toString(), money = BigDecimal(300), state = DepositState.Fail,
                createdTime = now)

        val data = listOf(t1, t2, t3)
        return CashDepositPage(data = data, total = 100)
    }

    fun generatorCashWithdrawPage(): CashWithdrawPage {


        val now = LocalDateTime.now()

        val w1 = CashWithdrawVo(orderId = UUID.randomUUID().toString(), money = BigDecimal(100), state = WithdrawState.Process,
                bankName = "工商银行", bankCardNumber = "6222222", name = "张三", createdTime = now, successfulTime = null, remark = null)
        val w2 = w1.copy(orderId = UUID.randomUUID().toString(), money = BigDecimal(200), state = WithdrawState.Successful,
                successfulTime = now, remark = "topup successful")
        val w3 = w1.copy(orderId = UUID.randomUUID().toString(), money = BigDecimal(300), state = WithdrawState.Fail)
        val data = listOf(w1, w2, w3)

        return CashWithdrawPage(data = data, total = 37)
    }


}

data class CashDepositQuery(

        @ApiModelProperty("订单Id")
        val orderId: String?,

        @ApiModelProperty("订单状态")
        val state: DepositState?

)

data class CashDepositPage(

        val data: List<CashDepositVo>,

        val total: Int
)

data class CashDepositVo(

        @ApiModelProperty("订单Id")
        val orderId: String,

        @ApiModelProperty("订单金额")
        val money: BigDecimal,

        @ApiModelProperty("充值状态")
        val state: DepositState,

        @ApiModelProperty("备注")
        val remark: String?,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime,

        @ApiModelProperty("订单成功时间")
        val successfulTime: LocalDateTime?

)

data class CashDepositReq(

        @ApiModelProperty("厅主银行卡Id")
        val clientBankId: Int,

        @ApiModelProperty("银行卡Id")
        val bankId: Int,

        @ApiModelProperty("银行卡号")
        val bankCardNumber: String,

        @ApiModelProperty("充值金额")
        val money: BigDecimal,

        @ApiModelProperty("截图证明")
        val uploadImage: String
)

data class CashDepositResp(

        @ApiModelProperty("订单Id")
        val orderId: String

)

data class CashWithdrawPage(

        val data: List<CashWithdrawVo>,

        val total: Int
)

data class CashWithdrawVo(

        @ApiModelProperty("订单Id")
        val orderId: String,

        @ApiModelProperty("订单金额")
        val money: BigDecimal,

        @ApiModelProperty("银行名称")
        val bankName: String,

        @ApiModelProperty("银行卡号")
        val bankCardNumber: String,

        @ApiModelProperty("提款人姓名")
        val name: String,

        @ApiModelProperty("取款状态")
        val state: WithdrawState,

        @ApiModelProperty("备注")
        val remark: String?,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime,

        @ApiModelProperty("订单成功时间")
        val successfulTime: LocalDateTime?
)

data class CashWithdrawReq(

        @ApiModelProperty("银行卡号")
        val bankId: Int,

        @ApiModelProperty("取款金额")
        val money: BigDecimal
)

data class CashWithdrawResp(

        @ApiModelProperty("订单Id")
        val orderId: String

)

data class CashTransferReq(

        @ApiModelProperty("转出钱包Id")
        val walletId: Int,

        @ApiModelProperty("转入钱包Id")
        val acceptWalletId: Int,

        @ApiModelProperty("转出金额")
        val money: BigDecimal

)

