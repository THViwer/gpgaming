package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.DepositState
import com.onepiece.gpgaming.beans.enums.PayState
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.WithdrawState
import com.onepiece.gpgaming.beans.value.internet.web.BankVo
import com.onepiece.gpgaming.beans.value.internet.web.CashValue
import com.onepiece.gpgaming.beans.value.internet.web.ClientBankVo
import com.onepiece.gpgaming.beans.value.internet.web.DepositValue
import com.onepiece.gpgaming.beans.value.internet.web.SelectPayVo
import com.onepiece.gpgaming.beans.value.internet.web.ThirdPayValue
import com.onepiece.gpgaming.beans.value.internet.web.WithdrawValue
import com.onepiece.gpgaming.player.controller.value.BalanceVo
import com.onepiece.gpgaming.player.controller.value.CashDepositResp
import com.onepiece.gpgaming.player.controller.value.CashWithdrawResp
import com.onepiece.gpgaming.player.controller.value.CheckBankResp
import com.onepiece.gpgaming.player.controller.value.CheckBetResp
import com.onepiece.gpgaming.player.controller.value.CheckPromotinResp
import com.onepiece.gpgaming.player.controller.value.DepositCoReq
import com.onepiece.gpgaming.player.controller.value.MemberBankCoReq
import com.onepiece.gpgaming.player.controller.value.MemberBankUoReq
import com.onepiece.gpgaming.player.controller.value.MemberBankVo
import com.onepiece.gpgaming.player.controller.value.MemberDailyReportValue
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

    @ApiOperation(tags = ["cash"], value = "????????????????????????")
    fun banks(): List<BankVo>

    @ApiOperation(tags = ["cash"], value = "???????????????")
    fun myBanks(): List<MemberBankVo>

    @ApiOperation(tags = ["cash"], value = "??????????????????????????????")
    fun checkBank(@RequestParam("bankCardNo") bankCardNo: String): CheckBankResp

    @ApiOperation(tags = ["cash"], value = "?????????????????????")
    fun checkBet(): CheckBetResp

    @ApiOperation(tags = ["cash"], value = "????????????")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun bankCreate(@RequestBody memberBankCoReq: MemberBankCoReq)

    @ApiOperation(tags = ["cash"], value = "????????????")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun bankUpdate(@RequestBody memberBankUoReq: MemberBankUoReq)

    @ApiOperation(tags = ["cash"], value = "????????????????????????????????????")
    fun payList(): SelectPayVo

    @ApiOperation(tags = ["cash"], value = "?????????????????????")
    fun clientBanks(): List<ClientBankVo>

    @ApiOperation(tags = ["cash"], value = "??????????????? -> ??????")
    fun thirdPay(): List<ThirdPayValue.SupportPay>

    @ApiOperation(tags = ["cash"], value = "??????????????? -> ????????????")
    fun selectPay(
            @RequestParam("payId") payId: Int,
            @RequestParam("amount") amount: BigDecimal,
            @RequestParam("responseUrl") responseUrl: String,
            @RequestParam("selectBank",  required = false) selectBank: Bank?
    ): ThirdPayValue.SelectPayResult

//    @ApiOperation(tags = ["cash"], value = "??????????????? -> ??????")
//    fun pays(
//            @RequestParam(value = "orderId", required = false) orderId: String?,
//            @RequestParam(value = "state", required = false) state: PayState?,
//            @RequestParam(value = "current", defaultValue = "0") current: Int,
//            @RequestParam(value = "size", defaultValue = "10") size: Int
//    ): Page<ThirdPayValue.OrderVo>

    @ApiOperation(tags = ["cash"], value = "???????????? -> ?????????+??????")
    fun pays(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate") endDate: LocalDate,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: PayState?
    ): List<ThirdPayValue.OrderVo>

    @ApiOperation(tags = ["cash"], value = "???????????? -> ??????")
    fun report(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate") endDate: LocalDate
    ): List<MemberDailyReportValue.ReportVo>

    @ApiOperation(tags = ["cash"], value = "????????????")
    fun deposit(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate") endDate: LocalDate,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: DepositState?,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<DepositValue.DepositVo>

    @ApiOperation(tags = ["cash"], value = "????????????")
    fun uploadProof(@RequestParam("file") file: MultipartFile): Map<String, String>

    @ApiOperation(tags = ["cash"], value = "??????")
    fun deposit(@RequestBody depositCoReq: DepositCoReq): CashDepositResp

    @ApiOperation(tags = ["cash"], value = "????????????")
    fun withdraw(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate") endDate: LocalDate,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: WithdrawState?,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<WithdrawValue.WithdrawVo>

    @ApiOperation(tags = ["cash"], value = "??????")
    fun withdraw(@RequestBody withdrawCoReq: WithdrawCoReq): CashWithdrawResp

//    @ApiOperation(tags = ["cash"], value = "?????? -> ??????")
//    fun checkWithdrawDetail(): CheckWithdrawDetail

    @ApiOperation(tags = ["cash"], value = "?????? -> ?????? ????????????????????????????????????")
    fun checkPromotion(
            @RequestParam("platform") platform: Platform,
            @RequestParam("amount") amount: BigDecimal,
            @RequestParam("promotionId", required = false) promotionId: Int?,
            @RequestParam("code", required = false) code: String?
    ): CheckPromotinResp

    @ApiOperation(tags = ["cash"], value = "??????")
    fun transfer(
            @RequestBody cashTransferReq: CashValue.CashTransferReq
    ): List<BalanceVo>

    @ApiOperation(tags = ["cash"], value = "???????????????????????????")
    fun transferToCenter(): List<CashValue.BalanceAllInVo>

    @ApiOperation(tags = ["cash"], value = "????????????")
    fun walletNote(
            @RequestParam(value = "onlyPromotion", defaultValue = "false") onlyPromotion: Boolean,
            @RequestParam(value = "events", required = false) events: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate", required = false) startDate: LocalDate?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate", required = false) endDate: LocalDate?,
            @RequestParam("current") current: Int,
            @RequestParam("size") size: Int
    ): List<WalletNoteVo>

    @ApiOperation(tags = ["cash"], value = "????????????(??????)")
    fun walletNotePage(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate") endDate: LocalDate,
            @RequestParam(value = "onlyPromotion", defaultValue = "false") onlyPromotion: Boolean,
            @RequestParam(value = "events", required = false) events: String?,
            @RequestParam("current") current: Int,
            @RequestParam("size") size: Int
    ): Page<WalletNoteVo>


    @ApiOperation(tags = ["cash"], value = "????????????")
    fun balance(
            @RequestHeader("platform") platform: Platform): BalanceVo

    @ApiOperation(tags = ["cash"], value = "??????????????????")
    fun balances(
            @RequestParam("category", required = false) category: PlatformCategory?
    ): List<BalanceVo>


}