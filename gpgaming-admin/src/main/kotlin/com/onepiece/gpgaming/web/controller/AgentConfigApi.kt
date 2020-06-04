package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.CommissionType
import com.onepiece.gpgaming.beans.model.Commission
import com.onepiece.gpgaming.beans.value.database.AgentValue
import com.onepiece.gpgaming.beans.value.database.CommissionValue
import com.onepiece.gpgaming.beans.value.internet.web.MemberValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.LocalDate

@Api(tags = ["agent"], description = "代理配置")
interface AgentConfigApi {

    @ApiOperation(tags = ["agent"], value = "佣金 -> 设置")
    fun commission(
            @RequestParam("/type") type: CommissionType
    ): List<Commission>

    @ApiOperation(tags = ["agent"], value = "佣金 -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun commissionCreate(
            @RequestBody co: CommissionValue.CommissionCo
    )

    @ApiOperation(tags = ["agent"], value = "佣金 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun commissionUpdate(
            @RequestBody uo: CommissionValue.CommissionUo
    )

//    @ApiOperation(tags = ["agent"], value = "代理 -> 列表")
//    fun agents(
//            @RequestParam("username") username: String,
//            @RequestParam("superiorUsername") superiorUsername: String
//    ): List<MemberValue.Agent>


    @ApiOperation(tags = ["agent"], value = "代理 -> 列表")
    fun agents(
            @RequestParam("superiorAgentId", required = false) superiorAgentId: Int?,
            @RequestParam("username", required = false) username: String?
    ): List<AgentValue.SubAgentVo>


    @ApiOperation(tags = ["agent"], value = "代理 -> 申请列表")
    fun applies(): List<MemberValue.Agent>

    @ApiOperation(tags = ["agent"], value = "代理 -> 审核")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun check(@RequestBody checkReq: AgentValue.AgentCheckReq)

    @ApiOperation(tags = ["agent"], value = "代理 -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@RequestBody req: AgentValue.AgentCoByAdmin)


    @ApiOperation(tags = ["agent"], value = "代理 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody req: MemberValue.AgentUo)


    @ApiOperation(tags = ["agent"], value = "佣金列表")
    fun commissions(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate
    ): List<AgentValue.AgentCommissionVo>

    @ApiOperation(tags = ["agent"], value = "下级代理佣金列表")
    fun subCommissions(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate,
            @RequestParam("agentId") agentId: Int
    ): List<AgentValue.AgentCommissionVo>


    @ApiOperation(tags = ["agent"], value = "会员佣金列表")
    fun memberCommissions(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate,
            @RequestParam("agentId") agentId: Int
    ): List<AgentValue.MemberCommissionVo>



}