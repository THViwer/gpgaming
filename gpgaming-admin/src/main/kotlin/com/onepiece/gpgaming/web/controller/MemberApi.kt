package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.MemberBank
import com.onepiece.gpgaming.beans.model.Vip
import com.onepiece.gpgaming.beans.value.database.VipValue
import com.onepiece.gpgaming.beans.value.internet.web.LevelCoReq
import com.onepiece.gpgaming.beans.value.internet.web.LevelMemberVo
import com.onepiece.gpgaming.beans.value.internet.web.LevelMoveDo
import com.onepiece.gpgaming.beans.value.internet.web.LevelUoReq
import com.onepiece.gpgaming.beans.value.internet.web.LevelVo
import com.onepiece.gpgaming.beans.value.internet.web.MemberBankValue
import com.onepiece.gpgaming.beans.value.internet.web.MemberCoReq
import com.onepiece.gpgaming.beans.value.internet.web.MemberPage
import com.onepiece.gpgaming.beans.value.internet.web.MemberUoReq
import com.onepiece.gpgaming.beans.value.internet.web.MemberValue
import com.onepiece.gpgaming.beans.value.internet.web.MemberWalletInfo
import com.onepiece.gpgaming.beans.value.internet.web.UserValue
import com.onepiece.gpgaming.beans.value.internet.web.WalletVo
import com.onepiece.gpgaming.core.daily.MemberDailyDetail
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.math.BigDecimal
import java.time.LocalDate

@Api(tags = ["user"], description = " ")
interface MemberApi {

    @ApiOperation(tags = ["user"], value = "会员 -> 列表")
    fun query(
            @RequestParam(value = "username", required = false) username: String?,
            @RequestParam(value = "name", required = false) name: String?,
            @RequestParam(value = "phone", required = false) phone: String?,
            @RequestParam(value = "levelId", required = false) levelId: Int?,
            @RequestParam(value = "status", required = false) status: Status?,
            @RequestParam(value = "promoteCode", required = false) promoteCode: String?,

            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): MemberPage

    @ApiOperation(tags = ["user"], value = "会员 -> 导出所有会员")
    fun excelMembers()

    @ApiOperation(tags = ["user"], value = "会员 -> 风险详情")
    fun checkRiskDetail(
            @PathVariable("memberId") memberId: Int
    ): MemberValue.RiskDetail


    @ApiOperation(tags = ["user"], value = "会员 -> 登陆")
    fun loginByAdmin(@RequestParam("username") username: String): UserValue.MemberLoginResponse

    @ApiOperation(tags = ["user"], value = "会员 -> 跟踪")
    fun follow(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "registerStartDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "registerEndDate") endDate: LocalDate
    ): List<MemberValue.FollowVo>

    @ApiOperation(tags = ["user"], value = "会员 -> 跟踪excel导出")
    fun followExcel(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "registerStartDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "registerEndDate") endDate: LocalDate
    )


    @ApiOperation(tags = ["user"], value = "会员 -> 详细信息")
    fun getWalletInfo(@RequestParam("memberId") memberId: Int): MemberWalletInfo

    @ApiOperation(tags = ["user"], value = "会员 -> 更新")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun update(@RequestBody memberUoReq: MemberUoReq)

    @ApiOperation(tags = ["user"], value = "会员 -> 创建")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody memberCoReq: MemberCoReq)

    @ApiOperation(tags = ["user"], value = "会员 -> 金额详情")
    fun balance(@PathVariable(value = "memberId") memberId: Int): WalletVo

    @ApiOperation(tags = ["user"], value = "会员 -> 银行卡")
    fun banks(@PathVariable(value = "memberId") memberId: Int): List<MemberBank>

    @ApiOperation(tags = ["user"], value = "会员 -> 银行卡修改")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun bankUo(@RequestBody req: MemberBankValue.MemberBankUo)


    @ApiOperation(tags = ["user"], value = "层级 -> 列表")
    fun all(): List<LevelVo>

    @ApiOperation(tags = ["user"], value = "层级 -> 可用列表")
    fun normalList(): List<LevelVo>

    @ApiOperation(tags = ["user"], value = "层级 -> 创建")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody levelCoReq: LevelCoReq)

    @ApiOperation(tags = ["user"], value = "层级 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody levelUoReq: LevelUoReq)



    @ApiOperation(tags = ["user"], value = "vip -> 列表")
    fun vipList(): List<Vip>

    @ApiOperation(tags = ["user"], value = "vip -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun vipCreate(@RequestBody co: VipValue.VipCo)

    @ApiOperation(tags = ["user"], value = "vip -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun vipUpdate(@RequestBody uo: VipValue.VipUo)




    @ApiOperation(tags = ["user"], value = "层级 -> 条件查询会员")
    fun findMembers(
            @RequestParam("username", required = false) username: String?,
            @RequestParam("levelId", required = false) levelId: Int?,
            @RequestParam("minBalance", required = false) minBalance: BigDecimal?,
            @RequestParam("maxBalance", required = false) maxBalance: BigDecimal?,
            @RequestParam("minTotalDepositBalance", required = false) minTotalDepositBalance: BigDecimal?,
            @RequestParam("maxTotalDepositBalance", required = false) maxTotalDepositBalance: BigDecimal?,
            @RequestParam("minTotalWithdrawBalance", required = false) minTotalWithdrawBalance: BigDecimal?,
            @RequestParam("maxTotalWithdrawBalance", required = false) maxTotalWithdrawBalance: BigDecimal?,
            @RequestParam("minTotalDepositFrequency", required = false) minTotalDepositFrequency: Int?,
            @RequestParam("maxTotalDepositFrequency", required = false) maxTotalDepositFrequency: Int?,
            @RequestParam("minTotalWithdrawFrequency", required = false) minTotalWithdrawFrequency: Int?,
            @RequestParam("maxTotalWithdrawFrequency", required = false) maxTotalWithdrawFrequency: Int?
    ): List<LevelMemberVo>

    @ApiOperation(tags = ["user"], value = "层级 -> 移动")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun move(@RequestBody levelMoveDo: LevelMoveDo)

    @ApiOperation(tags = ["user"], value = "电销 -> 会员转移")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun saleMove(
            @RequestParam("fromSaleId") fromSaleId: Int,
            @RequestParam("toSaleId") toSaleId: Int
    )

    @ApiOperation(tags = ["user"], value = "会员 -> 报表汇总详情")
    fun memberDailyReport(
            @RequestParam("memberId") memberId: Int,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "registerStartDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "registerEndDate") endDate: LocalDate
    ): MemberDailyDetail


}