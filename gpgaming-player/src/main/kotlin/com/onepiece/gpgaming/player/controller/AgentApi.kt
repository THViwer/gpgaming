package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.beans.value.database.AgentValue
import com.onepiece.gpgaming.player.controller.value.Contacts
import com.onepiece.gpgaming.player.controller.value.LoginReq
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.LocalDate

@Api(tags = ["agent"], description = " ")
interface AgentApi {

    @ApiOperation(tags = ["agent"], value = "代理注册")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun register(@RequestBody req: AgentValue.AgentRegisterReq)

    @ApiOperation(tags = ["agent"], value = "代理登陆")
    fun login(
            @RequestBody loginReq: LoginReq
    ): AgentValue.AgentLoginResp

    @ApiOperation(tags = ["agent"], value = "代理当前信息")
    fun info(): AgentValue.AgentInfo

    @ApiOperation(tags = ["api"], value = "联系我们")
    fun contactUs(): Contacts

    @ApiOperation(tags = ["agent"], value = "下级代理列表")
    fun subAgents(): List<AgentValue.SubAgentVo>

    @ApiOperation(tags = ["agent"], value = "佣金列表")
    fun commissions(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") endDate:  LocalDate
    ): List<AgentValue.AgentCommissionVo>

    @ApiOperation(tags = ["agent"], value = "下级代理佣金列表")
    fun subCommissions(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") endDate:  LocalDate,
            @RequestParam("superiorAgentId") superiorAgentId: Int
    ): List<AgentValue.AgentCommissionVo>


    @ApiOperation(tags = ["agent"], value = "会员佣金列表")
    fun memberCommissions(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") endDate:  LocalDate,
            @RequestParam("agentId") agentId: Int
    ): List<AgentValue.MemberCommissionVo>


}