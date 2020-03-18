package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.enums.DepositState
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.WithdrawState
import com.onepiece.gpgaming.beans.value.internet.web.BankVo
import com.onepiece.gpgaming.beans.value.internet.web.ClientBankVo
import com.onepiece.gpgaming.beans.value.internet.web.DepositVo
import com.onepiece.gpgaming.beans.value.internet.web.WithdrawVo
import com.onepiece.gpgaming.player.controller.value.BalanceAllInVo
import com.onepiece.gpgaming.player.controller.value.BalanceVo
import com.onepiece.gpgaming.player.controller.value.CashDepositResp
import com.onepiece.gpgaming.player.controller.value.CashTransferReq
import com.onepiece.gpgaming.player.controller.value.CashWithdrawResp
import com.onepiece.gpgaming.player.controller.value.CheckBankResp
import com.onepiece.gpgaming.player.controller.value.CheckBetResp
import com.onepiece.gpgaming.player.controller.value.CheckPromotinResp
import com.onepiece.gpgaming.player.controller.value.DepositCoReq
import com.onepiece.gpgaming.player.controller.value.MemberBankCoReq
import com.onepiece.gpgaming.player.controller.value.MemberBankUoReq
import com.onepiece.gpgaming.player.controller.value.MemberBankVo
import com.onepiece.gpgaming.player.controller.value.WalletNoteVo
import com.onepiece.gpgaming.player.controller.value.WithdrawCoReq
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

@Api(tags = ["cash"], description = " ")
interface CashApi {

    @ApiOperation(tags = ["cash"], value = "可支持银行卡列表")
    fun banks(
            @RequestHeader("launch") launch: LaunchMethod
    ): List<BankVo>

    @ApiOperation(tags = ["cash"], value = "我的银行卡")
    fun myBanks(): List<MemberBankVo>

    @ApiOperation(tags = ["cash"], value = "检查银行卡号是否存在")
    fun checkBank(@RequestParam("bankCardNo") bankCardNo: String): CheckBankResp

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
            @RequestHeader("language") language: Language,
            @RequestParam("platform") platform: Platform,
            @RequestParam("amount") amount: BigDecimal,
            @RequestParam("promotionId", required = false) promotionId: Int?
    ): CheckPromotinResp

    @ApiOperation(tags = ["cash"], value = "转账")
    fun transfer(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestBody cashTransferReq: CashTransferReq
    ): List<BalanceVo>

    @ApiOperation(tags = ["cash"], value = "转账所有平台到中心")
    fun transferToCenter(): List<BalanceAllInVo>

    @ApiOperation(tags = ["cash"], value = "钱包明细")
    fun walletNote(
            @RequestParam(value = "onlyPromotion", defaultValue = "false") onlyPromotion: Boolean,
            @RequestParam(value = "events", required = false) events: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate", required = false) startDate: LocalDate?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate", required = false) endDate: LocalDate?,
            @RequestParam("current") current: Int,
            @RequestParam("size") size: Int
    ): List<WalletNoteVo>

    @ApiOperation(tags = ["cash"], value = "钱包明细(分页)")
    fun walletNotePage(
            @RequestParam(value = "onlyPromotion", defaultValue = "false") onlyPromotion: Boolean,
            @RequestParam(value = "events", required = false) events: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate", required = false) startDate: LocalDate?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate", required = false) endDate: LocalDate?,
            @RequestParam("current") current: Int,
            @RequestParam("size") size: Int
    ): Page<WalletNoteVo>


    @ApiOperation(tags = ["cash"], value = "查询余额")
    fun balance(
            @RequestHeader("language") language: Language,
            @RequestHeader("platform") platform: Platform): BalanceVo

    @ApiOperation(tags = ["cash"], value = "查询所有余额")
    fun balances(
            @RequestHeader("language") language: Language,
            @RequestParam("category", required = false) category: PlatformCategory?
    ): List<BalanceVo>

}