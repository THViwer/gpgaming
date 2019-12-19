package com.onepiece.gpgaming.player.controller.value

import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal

data class CheckBetResp(
        @ApiModelProperty("当前打码量")
        val currentBet: BigDecimal,

        @ApiModelProperty("需要打码量")
        val needBet: BigDecimal,

        @ApiModelProperty("剩余打码量")
        val overBet: BigDecimal
)