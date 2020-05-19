package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.MemberAnalysisSort
import java.math.BigDecimal
import java.time.LocalDate

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
            val totalDepositAmount: BigDecimal,

            // 取款次数
            val totalWithdrawCount: Int,

            // 人工提存金额
            val totalArtificialAmount: BigDecimal,

            // 人工提存次数
            val totalArtificialCount: Int,

            // 自动入款金额
            val totalThirdPayAmount: BigDecimal,

            // 自动入款次数
            val totalThirdPayCount: Int,

            // 取款金额
            val totalWithdrawAmount: BigDecimal,

            // 返水金额
            val totalRebateAmount: BigDecimal,

            //  优惠金额
            val totalPromotionAmount: BigDecimal

    )

    data class AnalysisQuery(

            val startDate: LocalDate,

            val endDate: LocalDate,

            val clientId: Int,

            val sort: MemberAnalysisSort,

            val size: Int

    )

    data class AnalysisVo(

            // 厅主Id
            val clientId: Int,

            // 会员Id
            val memberId: Int,

            // 用户名
            val username: String = "",

            // 顾客盈利
            val totalMWin: BigDecimal,

            // 顾客下注
            val totalBet: BigDecimal,

            // 顾客熟
            val totalMLoss: BigDecimal,

            // 存款次数
            val depositCount: Int,

            // 充值金额
            val depositMoney: BigDecimal,

            // 取款次数
            val withdrawCount: Int,

            // 人工提存金额
            val artificialMoney: BigDecimal,

            // 人工提存次数
            val artificialCount: Int,

            // 取款金额
            val withdrawMoney: BigDecimal,

            // 返水金额
            val backwaterMoney: BigDecimal,

            //  优惠金额
            val promotionMoney: BigDecimal
    )

}