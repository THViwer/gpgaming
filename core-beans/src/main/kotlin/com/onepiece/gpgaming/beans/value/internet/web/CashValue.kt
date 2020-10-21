package com.onepiece.gpgaming.beans.value.internet.web

import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.DepositChannel
import com.onepiece.gpgaming.beans.enums.PayState
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.model.Deposit
import com.onepiece.gpgaming.beans.model.PayOrder
import com.onepiece.gpgaming.beans.model.Withdraw
import com.onepiece.gpgaming.beans.value.database.PayOrderValue
import io.swagger.annotations.ApiModelProperty
import springfox.documentation.annotations.ApiIgnore
import java.math.BigDecimal
import java.time.LocalDateTime

sealed class CashValue {

    enum class State {
        Process,

        Successful,

        Fail,

        Close

    }

    enum class Type {
        Deposit,

        Withdraw
    }

    data class CheckOrderReq(

            @ApiModelProperty("订单Id")
            val orderId: String,

            @ApiModelProperty("类型 充值 or 取款")
            val type: Type,

            @ApiModelProperty("状态")
            val state: State,

            @ApiModelProperty("备注")
            val remark: String?
    )

    data class CheckOrderLockReq(
            @ApiModelProperty("订单Id")
            val orderId: String,

            @ApiModelProperty("类型 充值 or 取款")
            val type: Type
    )

    data class CheckOrderVo(

            @ApiModelProperty("订单Id")
            val orderId: String,

            @ApiModelProperty("类型 充值 or 取款")
            val type: Type,

            @ApiModelProperty("操作金额")
            val money: BigDecimal,

            @ApiModelProperty("会员银行卡信息")
            val memberBank: BankDetail,

            @ApiModelProperty("厅主银行卡信息(取款无该参数)")
            val clientBank: BankDetail?,

            @ApiModelProperty("申请时间")
            val applicationTime: LocalDateTime,

            @ApiModelProperty("会员Id")
            val memberId: Int,

            @ApiModelProperty("用户名")
            val username: String,

            @ApiModelProperty("锁定人员客服Id")
            val lockWaiterId: Int?,

            @ApiModelProperty("状态")
            val state: State,

            @ApiModelProperty("备注")
            val remark: String?

    ) {

        data class BankDetail(
                @ApiModelProperty("银行卡Id")
                val bankId: Int,

                @ApiModelProperty("银行")
                val bank: Bank,

                @ApiModelProperty("姓名")
                val name: String,

                @ApiModelProperty("银行卡号")
                val bankCardNumber: String,

                @ApiModelProperty("存款渠道")
                val channel: DepositChannel?,

                @ApiModelProperty("上传图片地址")
                val imgPath: String?
        )

        companion object {

            fun of(deposit: Deposit): CheckOrderVo {

                val clientBank = BankDetail(bankId = deposit.clientBankId, bank = deposit.clientBank, name = deposit.clientBankName, bankCardNumber = deposit.clientBankCardNumber,
                        channel = null, imgPath = null)
                val memberBank = BankDetail(bankId = deposit.memberId, bank = deposit.memberBank, name = deposit.memberName, bankCardNumber = deposit.memberBankCardNumber,
                        channel = deposit.channel, imgPath = deposit.imgPath)

                return CheckOrderVo(orderId = deposit.orderId, type = Type.Deposit, money = deposit.money, memberId = deposit.memberId, username = deposit.username,
                        memberBank = memberBank, clientBank = clientBank, applicationTime = deposit.depositTime, lockWaiterId = deposit.lockWaiterId,
                        state = State.valueOf(deposit.state.toString()), remark = deposit.remarks)
            }

            fun of(withdraw: Withdraw): CheckOrderVo {
                val memberBank = BankDetail(bankId = withdraw.memberId, bank = withdraw.memberBank, name = withdraw.memberName, bankCardNumber = withdraw.memberBankCardNumber,
                        channel = null, imgPath = null)

                return CheckOrderVo(orderId = withdraw.orderId, type = Type.Withdraw, money = withdraw.money, memberId = withdraw.memberId, username = withdraw.username,
                        memberBank = memberBank, clientBank = null, applicationTime = withdraw.createdTime, lockWaiterId = withdraw.lockWaiterId,
                        state = State.valueOf(withdraw.state.toString()), remark = withdraw.remarks)
            }

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

    data class CashTransferReq(

            @ApiModelProperty("转出钱包")
            val from: Platform,

            @ApiModelProperty("转入的钱包")
            val to: Platform,

            @ApiModelProperty("金额")
            val amount: BigDecimal,

            @ApiModelProperty("参加优惠活动Id")
            var promotionId: Int?,

            @ApiModelProperty("优惠码")
            val code: String?
    )

    data class ThirdPayResponse(

            @ApiIgnore
            @JsonIgnore
            val summaries: List<PayOrderValue.ThirdPaySummary>,

            @ApiModelProperty("列表")
            val data: List<PayOrder>
    ) {

        val bankSummaries: List<ThirdPayBankTotal>
            @ApiModelProperty("汇总")
            get() {
                return summaries.groupBy { it.bank }.map {

                    val successfulAmount = it.value.firstOrNull { it.state == PayState.Successful }?.totalAmount ?: BigDecimal.ZERO
                    val processAmount = it.value.firstOrNull { it.state == PayState.Process }?.totalAmount ?: BigDecimal.ZERO
                    val closeAmount = it.value.firstOrNull { it.state == PayState.Close }?.totalAmount ?: BigDecimal.ZERO
                    val failedAmount = it.value.firstOrNull { it.state == PayState.Failed }?.totalAmount ?: BigDecimal.ZERO

                    ThirdPayBankTotal(bank = it.key, successfulAmount = successfulAmount, processAmount = processAmount, closeAmount = closeAmount, failedAmount = failedAmount)
                }
            }


        data class ThirdPayBankTotal(

                val bank: Bank,

                val successfulAmount: BigDecimal,

                val closeAmount: BigDecimal,

                val failedAmount: BigDecimal,

                val processAmount: BigDecimal

        )
    }

}