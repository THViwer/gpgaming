package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.internet.web.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.math.BigDecimal

@Api(tags = ["user"], description = " ")
interface LevelApi {

    @ApiOperation(tags = ["user"], value = "层级 -> 列表")
    fun all(): List<LevelVo>

    @ApiOperation(tags = ["user"], value = "层级 -> 可用列表")
    fun normalList(): List<LevelVo>

    @ApiOperation(tags = ["user"], value = "层级 -> 创建")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody levelCoReq: LevelCoReq)

    @ApiOperation(tags = ["user"], value = "层级 -> 更新")
    fun update(@RequestBody levelUoReq: LevelUoReq)

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

    @ApiOperation(tags = ["user"], value = "层级 -> 移动(未实现)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun move(@RequestBody levelMoveDo: LevelMoveDo)

//    @ApiOperation(tags = ["user"], value = "层级 -> 检查移动是否完成(未实现)")
//    fun checkMove(@PathVariable("sequence") sequence: String): LevelMoveCheckVo

}