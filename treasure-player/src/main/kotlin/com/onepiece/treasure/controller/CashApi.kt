package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.enums.*
import com.onepiece.treasure.beans.value.internet.web.ClientBankVo
import com.onepiece.treasure.beans.value.internet.web.DepositVo
import com.onepiece.treasure.beans.value.internet.web.WithdrawVo
import com.onepiece.treasure.controller.value.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Api(tags = ["cash"], description = " ")
interface CashApi {

    @ApiOperation(tags = ["cash"], value = "可支持银行卡列表")
    fun banks(): List<BankVo>

    @ApiOperation(tags = ["cash"], value = "我的银行卡")
    fun myBanks(): List<MemberBankVo>

    @ApiOperation(tags = ["cash"], value = "取款检查打码量")
    fun checkBet(): CheckBetResp

    @ApiOperation(tags = ["cash"], value = "银行创建")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun bankCreate(@RequestBody memberBankCoReq: MemberBankCoReq)

    @ApiOperation(tags = ["cash"], value = "银行修改")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun bankUpdate(@RequestBody memberBankUoReq: MemberBankUoReq)

    @ApiOperation(tags = ["cash"], value = "厅主银行卡列表")
    fun clientBanks(): List<ClientBankVo>

    @ApiOperation(tags = ["cash"], value = "充值列表")
    fun deposit(
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: DepositState?,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<DepositVo>

    @ApiOperation(tags = ["cash"], value = "上传图片")
    fun uploadProof(@RequestParam("file") file: MultipartFile): Map<String, String>

    @ApiOperation(tags = ["cash"], value = "充值")
    fun deposit(@RequestBody depositCoReq: DepositCoReq): CashDepositResp

    @ApiOperation(tags = ["cash"], value = "取款列表")
    fun withdraw(
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: WithdrawState?,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<WithdrawVo>

    @ApiOperation(tags = ["cash"], value = "取款")
    fun withdraw(@RequestBody withdrawCoReq: WithdrawCoReq): CashWithdrawResp

    @ApiOperation(tags = ["cash"], value = "中心 -> 平台 检查是否有优惠活动并提示")
    fun checkPromotion(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestParam("platform") platform: Platform,
            @RequestParam("amount") amount: BigDecimal,
            @RequestParam("promotionId", required = false) promotionId: Int?
    ): CheckPromotinResp

    @ApiOperation(tags = ["cash"], value = "转账")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun transfer(@RequestBody cashTransferReq: CashTransferReq)

    @ApiOperation(tags = ["cash"], value = "转账所以平台到中心")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun transferToCenter()

    @ApiOperation(tags = ["cash"], value = "钱包明细")
    fun walletNote(): List<WalletNoteVo>

    @ApiOperation(tags = ["cash"], value = "查询余额")
    fun balance(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestHeader("platform") platform: Platform): BalanceVo

    @ApiOperation(tags = ["cash"], value = "查询所有余额")
    fun balances(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestParam("category", required = false) category: PlatformCategory?
    ): List<BalanceVo>

}