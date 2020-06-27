package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.enums.DepositState
import com.onepiece.gpgaming.beans.enums.PayState
import com.onepiece.gpgaming.beans.enums.PayType
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.WithdrawState
import com.onepiece.gpgaming.beans.model.ArtificialOrder
import com.onepiece.gpgaming.beans.model.PayBind
import com.onepiece.gpgaming.beans.value.database.PayBindValue
import com.onepiece.gpgaming.beans.value.internet.web.BankVo
import com.onepiece.gpgaming.beans.value.internet.web.CashValue
import com.onepiece.gpgaming.beans.value.internet.web.ClientBankCoReq
import com.onepiece.gpgaming.beans.value.internet.web.ClientBankUoReq
import com.onepiece.gpgaming.beans.value.internet.web.ClientBankVo
import com.onepiece.gpgaming.beans.value.internet.web.DepositValue
import com.onepiece.gpgaming.beans.value.internet.web.TransferOrderValue
import com.onepiece.gpgaming.beans.value.internet.web.WalletNoteValue
import com.onepiece.gpgaming.beans.value.internet.web.WithdrawValue
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.LocalDate
import java.time.LocalDateTime

interface CashApi {

    @ApiOperation(tags = ["cash"], value = "银行列表")
    fun banks(): List<BankVo>

    @ApiOperation(tags = ["cash"], value = "厅主银行卡 -> 列表")
    fun all(): List<ClientBankVo>

    @ApiOperation(tags = ["cash"], value = "厅主银行卡 -> 创建")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody clientBankCoReq: ClientBankCoReq)

    @ApiOperation(tags = ["cash"], value = "厅主银行卡 -> 更新")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun update(@RequestBody clientBankUoReq: ClientBankUoReq)



    @ApiOperation(tags = ["cash"], value = "出入款 -> 列表")
    @Deprecated(message = "请使用出入款接口")
    fun check(): List<CashValue.CheckOrderVo>

    @ApiOperation(tags = ["cash"], value = "出入款 -> 锁定")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Deprecated(message = "请使用出入款接口")
    fun checkLock(@RequestBody req: CashValue.CheckOrderLockReq)

    @ApiOperation(tags = ["cash"], value = "出入款 -> 审核")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Deprecated(message = "请使用出入款接口")
    fun check(@RequestBody req: CashValue.CheckOrderReq)


    @ApiOperation(tags = ["cash"], value = "充值 -> 审核列表")
    fun deposit(): List<DepositValue.DepositVo>

    @ApiOperation(tags = ["cash"], value = "充值 -> 历史")
    fun deposit(
            @RequestParam(value = "state", required = false) state: DepositState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("endTime") endTime: LocalDateTime
    ): DepositValue.DepositTotal

    @ApiOperation(tags = ["cash"], value = "充值 -> 锁定")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun tryLock(@RequestParam("orderId") orderId: String)

    @ApiOperation(tags = ["cash"], value = "充值 -> 审核")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun check(@RequestBody depositUoReq: DepositValue.DepositUoReq)

    @ApiOperation(tags = ["cash"], value = "充值 -> 人工提存")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun artificial(@RequestBody artificialCoReq: DepositValue.ArtificialCoReq)

    @ApiOperation(tags = ["cash"], value = "充值 -> 人工提存列表")
    fun artificialList(
            @RequestParam("username", required = false) username: String?,
            @RequestParam("operatorUsername", required = false) operatorUsername: String?,
            @RequestParam("current") current: Int,
            @RequestParam("size") size: Int
    ): Page<ArtificialOrder>

    @ApiOperation(tags = ["cash"], value = "取款 -> 审核列表")
    fun withdraw(): List<WithdrawValue.WithdrawVo>

    @ApiOperation(tags = ["cash"], value = "取款 -> 历史")
    fun withdraw(
            @RequestParam(value = "state", required = false) state: WithdrawState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("endTime") endTime: LocalDateTime
    ): WithdrawValue.WithdrawTotal

    @ApiOperation(tags = ["cash"], value = "取款 -> 锁定")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun withdrawLock(@RequestParam("orderId") orderId: String)

    @ApiOperation(tags = ["cash"], value = "取款 -> 审核")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun withdrawCheck(@RequestBody withdrawUoReq: WithdrawValue.WithdrawUoReq)

    @ApiOperation(tags = ["cash"], value = "转账 -> 订单查询")
    fun query(
            @RequestParam("promotionId", required = false) promotionId: Int?,
            @RequestParam("memberId", required = false) memberId: Int?,
            @RequestParam("username", required = false) username: String?
    ): List<TransferOrderValue.TransferOrderVo>

    @ApiOperation(tags = ["cash"], value = "资金 -> 钱包操作列表")
    fun walletNoteList(
            @RequestParam("memberId") memberId: Int
    ): List<WalletNoteValue.WalletNoteVo>


    @ApiOperation(tags = ["cash"], value = "用户 -> 回收金额")
    fun retrieve(@RequestParam("memberId") memberId: Int): List<CashValue.BalanceAllInVo>

    @ApiOperation(tags = ["cash"], value = "用户 -> 强制清空平台优惠")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun constraintCleanPromotion(
            @RequestParam("memberId") memberId: Int,
            @RequestParam("platform") platform: Platform
    )

    @ApiOperation(tags = ["cash"], value = "支付平台 -> 列表")
    fun payBind(): List<PayBind>

    @ApiOperation(tags = ["cash"], value = "支付平台 -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun payBindCreate(@RequestBody req: PayBindValue.PayBindCo)

    @ApiOperation(tags = ["cash"], value = "支付平台 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun payBindUpdate(@RequestBody req: PayBindValue.PayBindUo)


    @ApiOperation(tags = ["cash"], value = "第三方订单 -> 列表")
    fun payOrder(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam("payType", required = false) payType: PayType?,
            @RequestParam("orderId", required = false) orderId: String?,
            @RequestParam("username", required = false) username: String?,
            @RequestParam("state", required = false) state: PayState?
    ): CashValue.ThirdPayResponse

    @ApiOperation(tags = ["cash"], value = "第三方订单 -> 入款")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun thirdPayCheck(
            @RequestParam("orderId") orderId: String,
            @RequestParam("remark") remark: String
    )

}