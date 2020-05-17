package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.player.controller.value.LoginReq
import com.onepiece.gpgaming.player.controller.value.LoginResp
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestBody

@Api(tags = ["AgentApi"], description = " ")
interface AgentApi {

    @ApiOperation(tags = ["AgentApi"], value = "代理登陆")
    fun login(
            @RequestBody loginReq: LoginReq
    ): LoginResp




}