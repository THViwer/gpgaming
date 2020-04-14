package com.onepiece.gpgaming.player.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation

@Api(tags = ["payment"], description = " ")
interface PayBackApi  {

    @ApiOperation(tags = ["payment"], value = "m3pay")
    fun m3pay()

    @ApiOperation(tags = ["payment"], value = "surepay")
    fun surepay()

}