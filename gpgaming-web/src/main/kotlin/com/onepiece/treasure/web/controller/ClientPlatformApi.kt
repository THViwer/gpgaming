package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.internet.web.PlatformVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation

@Api(tags = ["index"], description = "首页")
interface ClientPlatformApi {

    @ApiOperation(tags = ["index"], value = "厅主平台 -> 列表")
    fun all(): List<PlatformVo>

//    @ApiOperation(tags = ["setting"], value = "厅主平台 -> 更新")
//    @ResponseStatus(code = HttpStatus.NO_CONTENT)
//    fun update(@RequestBody platformUoReq: PlatformUoReq)

}