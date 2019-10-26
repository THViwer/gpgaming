package com.onepiece.treasure.controller.value

import com.onepiece.treasure.beans.enums.Bank
import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.WithdrawState
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

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

        @ApiModelProperty("充值银行")
        val memberBank: Bank,

        @ApiModelProperty("银行卡号")
        val memberBankCardNumber: String,

        @ApiModelProperty("存款人姓名")
        val memberName: String,

        @ApiModelProperty("上传图片地址")
        val imgPath: String,

        @ApiModelProperty("备注")
        val remark: String?,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime,

        @ApiModelProperty("订单成功时间")
        val endTime: LocalDateTime?

)

data class DepositCoReq(

        @ApiModelProperty("厅主银行卡Id")
        val clientBankId: Int,

        @ApiModelProperty("银行卡号")
        val memberBankCardNumber: String,

        @ApiModelProperty("充值银行")
        val memberBank: Bank,

        @ApiModelProperty("会员姓名")
        val memberName: String,

        @ApiModelProperty("充值金额")
        val money: BigDecimal,

        @ApiModelProperty("截图证明")
        val imgPath: String
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

data class WithdrawCoReq(

        @ApiModelProperty("银行卡号")
        val memberBankId: Int,

        @ApiModelProperty("取款金额")
        val money: BigDecimal,

        @ApiModelProperty("取款密码")
        val safetyPassword: String
)

data class CashWithdrawResp(

        @ApiModelProperty("订单Id")
        val orderId: String

)

data class CashTransferReq(

        @ApiModelProperty("转入钱包Id")
        val platform: Platform,

        @ApiModelProperty("TransferIn 中心 -> 平台，TransferOut 平台 -> 中心")
        val action: TransferAction,

        @ApiModelProperty("转出金额")
        val money: BigDecimal

)

enum class TransferAction {

        // 中心 -> 平台
        TransferIn,

        // 平台 -> 中心
        TransferOut
}

