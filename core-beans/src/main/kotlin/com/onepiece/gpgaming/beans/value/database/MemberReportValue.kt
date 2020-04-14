package com.onepiece.gpgaming.beans.value.database

import java.math.BigDecimal

sealed class MemberReportValue {

    data  class MemberReportTotal(

            // 总数
            val count: Int,

            // 顾客盈利
            val totalMWin: BigDecimal,

            // 顾客下注
            val totalBet: BigDecimal,

            // 转入金额
            val transferIn: BigDecimal,

            // 转出金额
            val transferOut: BigDecimal,

            // 存款次数
            val totalDepositCount: Int,

            // 充值金额
            val totalDepositMoney: BigDecimal,

            // 取款次数
            val totalWithdrawCount: Int,

            // 人工提存金额
            val totalArtificialMoney: BigDecimal,

            // 人工提存次数
            val totalArtificialCount: Int,

            // 自动入款金额
            val totalThirdPayMoney: BigDecimal,

            // 自动入款次数
            val totalThirdPayCount: Int,

            // 取款金额
            val totalWithdrawMoney: BigDecimal,

            // 返水金额
            val totalBackwaterMoney: BigDecimal,

            //  优惠金额
            val totalPromotionMoney: BigDecimal

    )

}