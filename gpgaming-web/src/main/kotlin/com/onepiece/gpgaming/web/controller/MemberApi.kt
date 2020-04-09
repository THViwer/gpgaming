package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.MemberBank
import com.onepiece.gpgaming.beans.value.internet.web.MemberBankValue
import com.onepiece.gpgaming.beans.value.internet.web.MemberCoReq
import com.onepiece.gpgaming.beans.value.internet.web.MemberPage
import com.onepiece.gpgaming.beans.value.internet.web.MemberUoReq
import com.onepiece.gpgaming.beans.value.internet.web.MemberWalletInfo
import com.onepiece.gpgaming.beans.value.internet.web.WalletVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["user"], description = " ")
interface MemberApi {

    @ApiOperation(tags = ["user"], value = "会员 -> 列表")
    fun query(
            @RequestParam(value = "username", required = false) username: String?,
            @RequestParam(value = "name", required = false) name: String?,
            @RequestParam(value = "phone", required = false) phone: String?,
            @RequestParam(value = "levelId", required = false) levelId: Int?,
            @RequestParam(value = "status", required = false) status: Status?,
            @RequestParam(value = "promoteSource", required = false) promoteSource: String?,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): MemberPage

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

}