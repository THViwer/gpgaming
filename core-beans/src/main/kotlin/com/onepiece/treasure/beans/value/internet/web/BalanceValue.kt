package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.utils.BalanceUtil
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal

object BalanceValueFactory {

    fun generatorBalanceDetail(): BalanceDetail {

        val w1 = WalletDetail(walletId = 1, balance = BigDecimal(10), name = "centerWallet", giftBalance = BigDecimal.ZERO,
                currentBet = BigDecimal.ZERO, demandBet = BigDecimal.ZERO)

        val w2 = w1.copy(walletId = 2, name = "AG-老虎机", balance = BigDecimal(10), giftBalance = BigDecimal.ONE,
                currentBet = BigDecimal.ONE, demandBet = BigDecimal(16))

        val w3 = w1.copy(walletId = 3, name = "SUN-捕鱼", balance = BigDecimal(20), giftBalance = BigDecimal.ZERO,
                currentBet = BigDecimal.ZERO, demandBet = BigDecimal.ZERO)

        val wallets = listOf(w1, w2, w3)

        return BalanceDetail(memberId = 1, wallets = wallets)
    }

}

data class BalanceDetail(
        @ApiModelProperty("会员Id")
        val memberId: Int,

        @ApiModelProperty("钱包列表")
        val wallets: List<WalletDetail>
) {

    val totalBalance: BigDecimal
        @ApiModelProperty("总金额")
        get() {
            return BalanceUtil.format(wallets.sumByDouble { it.balance.toDouble() })
        }

}

data class WalletDetail(

        @ApiModelProperty("钱包Id")
        val walletId: Int,

        @ApiModelProperty("钱包名称")
        val name: String,

        @ApiModelProperty("余额")
        val balance: BigDecimal,

        @ApiModelProperty("赠送金额")
        val giftBalance: BigDecimal,

        @ApiModelProperty("当前投注金额")
        val currentBet: BigDecimal,

        @ApiModelProperty("需要投注")
        val demandBet: BigDecimal
)