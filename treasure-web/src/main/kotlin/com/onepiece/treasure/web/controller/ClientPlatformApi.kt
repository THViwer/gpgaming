package com.onepiece.treasure.web.controller

import com.onepiece.treasure.web.controller.value.PlatformUo
import com.onepiece.treasure.web.controller.value.PlatformVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["setting"], description = " ")
interface ClientPlatformApi {

    @ApiOperation(tags = ["setting"], value = "platform -> all")
    fun all(): List<PlatformVo>

    @ApiOperation(tags = ["setting"], value = "platform -> update")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun update(@RequestBody platformUo: PlatformUo)

}