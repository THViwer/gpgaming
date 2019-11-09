package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.internet.web.PlatformUoReq
import com.onepiece.treasure.beans.value.internet.web.PlatformVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["setting"], description = " ")
interface ClientPlatformApi {

    @ApiOperation(tags = ["setting"], value = "厅主平台 -> 列表")
    fun all(): List<PlatformVo>

    @ApiOperation(tags = ["setting"], value = "厅主平台 -> 更新")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun update(@RequestBody platformUoReq: PlatformUoReq)

}