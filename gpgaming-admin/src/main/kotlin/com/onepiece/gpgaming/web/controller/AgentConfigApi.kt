package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.CommissionType
import com.onepiece.gpgaming.beans.model.Commission
import com.onepiece.gpgaming.beans.value.database.CommissionValue
import com.onepiece.gpgaming.beans.value.internet.web.MemberValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["bet"], description = "代理配置")
interface AgentConfigApi {

    @ApiOperation(tags = ["cash"], value = "佣金 -> 设置")
    fun commission(
            @RequestParam("/type") type: CommissionType
    ): List<Commission>

    @ApiOperation(tags = ["cash"], value = "佣金 -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun commissionCreate(
            @RequestBody co: CommissionValue.CommissionCo
    )

    @ApiOperation(tags = ["cash"], value = "佣金 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun commissionUpdate(
            @RequestBody uo: CommissionValue.CommissionUo
    )

    @GetMapping("/agents")
    fun agents(
            @RequestParam("username") username: String,
            @RequestParam("superiorUsername") superiorUsername: String
    ): List<MemberValue.Agent>


}