package com.onepiece.treasure.web.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PathVariable

@Api(tags = ["user"], description = " ")
interface UserApi {

    @ApiOperation(tags = ["user"], value = "测试")
    fun get(@PathVariable("id") id: Int): UserVo

}