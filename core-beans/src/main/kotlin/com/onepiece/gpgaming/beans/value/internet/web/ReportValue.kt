package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.ClientDailyReport
import com.onepiece.gpgaming.beans.model.ClientPlatformDailyReport
import com.onepiece.gpgaming.beans.model.PromotionPlatformDailyReport
import com.onepiece.gpgaming.beans.model.TransferOrder
import com.onepiece.gpgaming.beans.value.database.MemberReportValue
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal

sealed class ReportValue {

    data class MemberTotalReport(

            @ApiModelProperty("数据列表")
            val data: List<MemberPlatformReportWebVo> = emptyList()

    ) {

        companion object {
            fun empty(): MemberTotalReport {
                return MemberTotalReport(emptyList())
            }
        }

        val totalTransferIn: BigDecimal
            @ApiModelProperty("总转入金额")
            get() {
                return data.sumByDouble { it.transferIn.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalTransferOut: BigDecimal
            @ApiModelProperty("总转出金额")
            get() {
                return data.sumByDouble { it.transferOut.toDouble() }.toBigDecimal().setScale(2, 2)
            }
    }

    data class MemberTotalDetailReport(

            @ApiModelProperty("汇总详情 ")
            val memberReportTotal: MemberReportValue.MemberReportTotal,

            @ApiModelProperty("数据列表")
            val data: List<MemberReportWebVo>
    ) {

//        val totalTransferIn: BigDecimal
//            @ApiModelProperty("总转入金额")
//            get() {
//                return data.sumByDouble { it.transferIn.toDouble() }.toBigDecimal().setScale(2, 2)
//            }
//
//        val totalTransferOut: BigDecimal
//            @ApiModelProperty("总转出金额")
//            get() {
//                return data.sumByDouble { it.transferOut.toDouble() }.toBigDecimal().setScale(2, 2)
//            }
//
//        val totalDepositMoney: BigDecimal
//            @ApiModelProperty("总充值金额")
//            get() {
//                return data.sumByDouble { it.depositMoney.toDouble() }.toBigDecimal().setScale(2, 2)
//            }
//
//        val totalWithdrawMoney: BigDecimal
//            @ApiModelProperty("总取款金额")
//            get() {
//                return data.sumByDouble { it.withdrawMoney.toDouble() }.toBigDecimal().setScale(2, 2)
//            }
//
//        val totalArtificialMoney: BigDecimal
//            @ApiModelProperty("总人工提存金额")
//            get() {
//                return data.sumByDouble { it.artificialMoney.toDouble() }.toBigDecimal().setScale(2, 2)
//            }
//
//        val totalThirdPayMoney: BigDecimal
//            @ApiModelProperty("总三方充值金额")
//            get() {
//                return data.sumByDouble { it.thirdPayMoney.toDouble() }.toBigDecimal().setScale(2, 2)
//            }
//
//        val totalThirdPayCount: Int
//            @ApiModelProperty("总三方充值次数")
//            get() {
//                return data.sumBy { it.thirdPayCount }
//            }
//
//        val totalArtificialCount: Int
//            @ApiModelProperty("总人工提存次数")
//            get() {
//                return data.sumBy { it.artificialCount }
//            }
//
//        val totalBet: BigDecimal
//            @ApiModelProperty("下注金额")
//            get() {
//                return data.sumByDouble { it.totalBet.toDouble() }.toBigDecimal().setScale(2, 2)
//            }
//
//        val totalMWin: BigDecimal
//            @ApiModelProperty("盈利金额")
//            get() {
//                return data.sumByDouble { it.totalMWin.toDouble() }.toBigDecimal().setScale(2, 2)
//            }
//
//        val totalCWin: BigDecimal
//            @ApiModelProperty("业主盈利金额")
//            get() {
//                return totalBet.minus(totalMWin)
//            }

    }

    data class CTotalReport(

            @ApiModelProperty("数据列表")
            val data: List<ClientDailyReport>

    ) {

        val totalTransferIn: BigDecimal
            @ApiModelProperty("总转入金额")
            get() {
                return data.sumByDouble { it.transferIn.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalTransferOut: BigDecimal
            @ApiModelProperty("总转出金额")
            get() {
                return data.sumByDouble { it.transferOut.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalPromotionAmount: BigDecimal
            @ApiModelProperty("总优惠金额")
            get() {
                return data.sumByDouble { it.promotionAmount.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalDepositCount: BigDecimal
            @ApiModelProperty("充值次数")
            get() {
                return data.sumByDouble { it.depositCount.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalDepositAmount: BigDecimal
            @ApiModelProperty("总充值金额")
            get() {
                return data.sumByDouble { it.depositAmount.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalWithdrawCount: BigDecimal
            @ApiModelProperty("总取款次数")
            get() {
                return data.sumByDouble { it.withdrawCount.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalWithdrawAmount: BigDecimal
            @ApiModelProperty("总取款金额")
            get() {
                return data.sumByDouble { it.withdrawAmount.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalNewMemberCount: Int
            @ApiModelProperty("总新增人数")
            get() {
                return data.sumBy { it.newMemberCount }
            }

        val totalThirdPayAmount: BigDecimal
            @ApiModelProperty("总三方充值金额")
            get() {
                return data.sumByDouble { it.thirdPayAmount.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalThirdPayCount: Int
            @ApiModelProperty("总新增人数")
            get() {
                return data.sumBy { it.thirdPayCount }
            }

        val totalArtificialAmount: BigDecimal
            @ApiModelProperty("总人工提存金额")
            get() {
                return data.sumByDouble { it.artificialAmount.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalArtificialCount: Int
            @ApiModelProperty("总人工提存次数")
            get() {
                return data.sumBy { it.artificialCount }
            }

        val totalBet: BigDecimal
            @ApiModelProperty("下注金额")
            get() {
                return data.sumByDouble { it.totalBet.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalMWin: BigDecimal
            @ApiModelProperty("盈利金额")
            get() {
                return data.sumByDouble { it.totalMWin.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalCWin: BigDecimal
            @ApiModelProperty("业主盈利金额")
            get() {
                return totalBet.minus(totalMWin)
            }

        val totalRebateAmount: BigDecimal
            @ApiModelProperty("总返水金额")
            get() {
                return data.sumByDouble { it.rebateAmount.toDouble() }.toBigDecimal().setScale(2, 2)
            }


    }

    data class CPTotalReport (

            @ApiModelProperty("数据列表")
            val data: List<ClientPlatformDailyReport>

    ) {

        val totalBet: BigDecimal
            @ApiModelProperty("总下注金额")
            get() {
                return data.sumByDouble { it.bet.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalMWin: BigDecimal
            @ApiModelProperty("玩家总盈利")
            get() {
                return data.sumByDouble { it.win.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalCWin: BigDecimal
            @ApiModelProperty("业主总盈利")
            get() {
                return totalBet.minus(totalMWin)
            }

        val totalTransferIn: BigDecimal
            @ApiModelProperty("总转入金额")
            get() {
                return data.sumByDouble { it.transferIn.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalTransferOut: BigDecimal
            @ApiModelProperty("总转出金额")
            get() {
                return data.sumByDouble { it.transferOut.toDouble() }.toBigDecimal().setScale(2, 2)
            }

        val totalPromotionAmount: BigDecimal
            @ApiModelProperty("总优惠金额")
            get() {
                return data.sumByDouble { it.promotionAmount.toDouble() }.toBigDecimal().setScale(2, 2)
            }

    }

    data class PromotionTotalReport(
            @ApiModelProperty("数据列表")
            val data: List<PromotionReportValue.PromotionReportVo>
    ) {

        val totalPromotionAmount: BigDecimal
            @ApiModelProperty("总优惠金额")
            get() {
                return data.sumByDouble { it.promotionAmount.toDouble() }.toBigDecimal().setScale(2, 2)
            }

    }

    data class PromotionCTotalReport(
            @ApiModelProperty("数据列表")
            val data: List<PromotionPlatformDailyReport>
    ) {

        val totalPromotionAmount: BigDecimal
            @ApiModelProperty("总优惠金额")
            get() {
                return data.sumByDouble { it.promotionAmount.toDouble() }.toBigDecimal().setScale(2, 2)
            }

    }

    data class PromotionMTotalReport(
            @ApiModelProperty("数据列表")
            val data: List<TransferOrder>
    ) {

        val totalMoney: BigDecimal
            @ApiModelProperty("总转出金额")
            get() {
                return data.sumByDouble { it.money.toDouble() }.toBigDecimal().setScale(2, 2)
            }


        val totalPromotionAmount: BigDecimal
            @ApiModelProperty("总优惠金额")
            get() {
                return data.sumByDouble { it.promotionAmount.toDouble() }.toBigDecimal().setScale(2, 2)
            }


    }

    data class PlatformSettleVo(

            // 会员Id
            val memberId:  Int,

            // 用户名
            val username: String,

            // 平台
            val platform: Platform,

            // 下注
            val bet: BigDecimal = BigDecimal.ZERO,

            // 有效投注
            val validBet: BigDecimal = BigDecimal.ZERO,

            // 顾客盈利
            val mwin: BigDecimal = BigDecimal.ZERO,

            // 反水
            val rebate: BigDecimal  = BigDecimal.ZERO
    ) {

        val cwin: BigDecimal = mwin.negate()

    }

}