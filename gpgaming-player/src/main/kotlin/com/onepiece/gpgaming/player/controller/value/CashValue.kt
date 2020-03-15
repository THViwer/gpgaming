package com.onepiece.gpgaming.player.controller.value

import com.fasterxml.jackson.annotation.JsonFormat
import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.DepositChannel
import com.onepiece.gpgaming.beans.enums.DepositState
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.WalletEvent
import com.onepiece.gpgaming.beans.enums.WithdrawState
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.time.LocalDateTime

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

        @ApiModelProperty("转账日期,格式：yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        val depositTime: LocalDateTime,

        @ApiModelProperty("充值方式")
        val channel: DepositChannel,

        @ApiModelProperty("会员银行卡Id")
        val memberBankId: Int?,

        @ApiModelProperty("充值银行")
        val memberBank: Bank,

        @ApiModelProperty("银行卡号")
        val memberBankCardNumber: String,

        @ApiModelProperty("充值金额")
        val money: BigDecimal,

        @ApiModelProperty("截图证明")
        val imgPath: String?
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
        val memberBankId: Int?,

        @ApiModelProperty("银行")
        val bank: Bank,

        @ApiModelProperty("银行卡号")
        val bankCardNumber: String,

        @ApiModelProperty("取款金额")
        val money: BigDecimal

//        @ApiModelProperty("取款密码")
//        val safetyPassword: String
)

data class CashWithdrawResp(

        @ApiModelProperty("订单Id")
        val orderId: String

)
data class CheckPromotionVo(

        @ApiModelProperty("优惠活动Id")
        val promotionId: Int,

        @ApiModelProperty("标题")
        val title: String,

        @ApiModelProperty("优惠活动介绍")
        val promotionIntroduction: String

)

data class CheckPromotinResp(

        @ApiModelProperty("优惠活动列表")
        val promotions: List<CheckPromotionVo>
) {
        val promotion: Boolean
                @ApiModelProperty("是否有优惠活动")
                get() = promotions.isNotEmpty()

}


data class CashTransferReq(

        @ApiModelProperty("转出钱包")
        val from: Platform,

        @ApiModelProperty("转入的钱包")
        val to: Platform,

        @ApiModelProperty("金额")
        val amount: BigDecimal,

        @ApiModelProperty("参加优惠活动Id")
        var promotionId: Int?

)

data class CashTransferResp(

        @ApiModelProperty("转出平台的余额")
        val fromBalance: BigDecimal,

        @ApiModelProperty("转平台的余额")
        val toBalance: BigDecimal
)

data class BalanceVo(

        @ApiModelProperty("中心钱包余额")
        val centerBalance: BigDecimal,

        @ApiModelProperty("平台")
        val platform: Platform,

        @ApiModelProperty("余额")
        val balance: BigDecimal,

        @ApiModelProperty("是否可以转入转出")
        val transfer: Boolean,

        @ApiModelProperty("提示信息(一般参加活动时无法提款才会有)")
        val tips: String?

//        @ApiModelProperty("是否可以转入 中心 -> 平台")
//        val transferIn: Boolean

) {
        val category: PlatformCategory
                @ApiModelProperty("平台类型")
                get() {
                        return platform.category
                }

        val pname: String
                @ApiModelProperty("平台名称")
                get() {
                        return platform.pname
                }

}

data class BalanceAllInVo(
        @ApiModelProperty("平台名称")
        val platform: Platform,

        @ApiModelProperty("余额")
        val balance: BigDecimal
) {

        val category: PlatformCategory
                @ApiModelProperty("平台类型")
                get() {
                        return platform.category
                }

}

data class WalletNoteVo(

        val id: Int,

        @ApiModelProperty("会员Id")
        val memberId: Int,

        @ApiModelProperty("事件Id")
        val eventId: String?,

        @ApiModelProperty("事件")
        val event: WalletEvent,

        @ApiModelProperty("操作金额")
        val money: BigDecimal,

        @ApiModelProperty("优惠金额")
        val promotionMoney: BigDecimal?,

        @ApiModelProperty("备注")
        val remarks: String,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime
)

//enum class TransferAction {
//
//        // 中心 -> 平台
//        TransferIn,
//
//        // 平台 -> 中心
//        TransferOut
//}

