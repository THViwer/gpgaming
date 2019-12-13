package com.onepiece.treasure.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation

@Api(tags = ["open"], description = " ")
interface OpenApi {

    @ApiOperation(tags = ["open"], value = "gameplay login")
    fun gamePlayLogin(): String


}