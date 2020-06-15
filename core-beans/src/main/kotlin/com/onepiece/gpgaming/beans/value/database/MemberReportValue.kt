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

    data class CollectQuery(

            val bossId: Int,

            val clientId: Int,

            val startDate: LocalDate,

            val endDate: LocalDate,

            val agentId: Int

    )

    data class MemberMonthReport(
            // 日期
            val day: LocalDate,

            // bossId
            val bossId: Int,

            // 厅主Id
            val clientId: Int,

            // 上级代理
            val superiorAgentId: Int,

            // 代理
            val agentId: Int,

            // 会员Id
            val memberId: Int,

            // 用户名
            val username: String,

            // 顾客盈利
            val totalMWin: BigDecimal,

            // 顾客下注
            val totalBet: BigDecimal,

            // 转入金额
            val transferIn: BigDecimal,

            // 转出金额
            val transferOut: BigDecimal,

            // 存款次数
            val depositCount: Int,

            // 充值金额
            val depositAmount: BigDecimal,

            // 取款次数
            val withdrawCount: Int,

            // 人工提存金额
            val artificialAmount: BigDecimal,

            // 人工提存次数
            val artificialCount: Int,

            // 自动入款金额
            val thirdPayAmount: BigDecimal,

            // 自动入款次数
            val thirdPayCount: Int,

            // 取款金额
            val withdrawAmount: BigDecimal,

            //  优惠金额
            val promotionAmount: BigDecimal,

            // 返水金额
            val rebateAmount: BigDecimal
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
            val depositAmount: BigDecimal,

            // 取款次数
            val withdrawCount: Int,

            // 人工提存金额
            val artificialAmount: BigDecimal,

            // 人工提存次数
            val artificialCount: Int,

            // 取款金额
            val withdrawAmount: BigDecimal,

            // 返水金额
            val rebateAmount: BigDecimal,

            //  优惠金额
            val promotionAmount: BigDecimal
    )

}