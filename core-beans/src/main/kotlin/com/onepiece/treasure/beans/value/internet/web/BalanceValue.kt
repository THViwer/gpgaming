package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.Platform
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal

object BalanceValueFactory {

//    fun generatorBalanceDetail(): BalanceDetail {
//
//        val w1 = WalletDetail(walletId = 1, balance = BigDecimal(10), name = "centerWallet", giftBalance = BigDecimal.ZERO,
//                currentBet = BigDecimal.ZERO, demandBet = BigDecimal.ZERO)
//
//        val w2 = w1.copy(walletId = 2, name = "AG-老虎机", balance = BigDecimal(10), giftBalance = BigDecimal.ONE,
//                currentBet = BigDecimal.ONE, demandBet = BigDecimal(16))
//
//        val w3 = w1.copy(walletId = 3, name = "SUN-捕鱼", balance = BigDecimal(20), giftBalance = BigDecimal.ZERO,
//                currentBet = BigDecimal.ZERO, demandBet = BigDecimal.ZERO)
//
//        val wallets = listOf(w1, w2, w3)
//
//        return BalanceDetail(memberId = 1, wallets = wallets)
//    }

}

data class WalletVo(

        @ApiModelProperty("钱包Id")
        val id: Int,

        @ApiModelProperty("会员Id")
        val memberId: Int,

        @ApiModelProperty("钱包名称")
        val platform: Platform,

        @ApiModelProperty("余额")
        val balance: BigDecimal,

        @ApiModelProperty("冻结金额")
        val freezeBalance: BigDecimal,

        @ApiModelProperty("总充值金额")
        val totalBalance: BigDecimal,

        @ApiModelProperty("总优惠金额")
        val totalGiftBalance: BigDecimal


)
