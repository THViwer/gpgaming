package com.onepiece.gpgaming.player.controller.value

import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal

data class CheckBetResp(
        @ApiModelProperty("当前打码量")
        val currentBet: BigDecimal,

        @ApiModelProperty("需要打码量")
        val needBet: BigDecimal,

        @ApiModelProperty("剩余打码量")
        val overBet: BigDecimal,

        @ApiModelProperty("昨日返水")
        val yesRebate:  BigDecimal,

        @ApiModelProperty("今日打码")
        val todayBet:  BigDecimal,

        @ApiModelProperty("今日剩余出款金额")
        val lastWithdraw: BigDecimal,


        @ApiModelProperty("总充值金额")
        val totalDeposit: BigDecimal,

        // 校验标准 totalDeposit > minWithdrawRequire 时可出款
        @ApiModelProperty("最低出款要求 (出款必须有充值 且充值过的金额 大于 该值)")
        val minWithdrawRequire: BigDecimal
)