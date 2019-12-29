package com.onepiece.gpgaming.beans.enums

import com.onepiece.gpgaming.beans.model.Promotion
import com.onepiece.gpgaming.beans.model.TransferOrder
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

enum class PromotionPeriod {

    // 每天一次
    DailyOnce,

    // 每天
    Daily,

    // 周日
    Sunday,

    // 工作日
    WorkingDay,

    // 周末
    Weekend,

    // 每周一次
    WeeklyOnce,

    // 每周
    Weekly,

    // 每月一次
    MonthlyOnce,

    // 每月
    Monthly,

    // 不限
    Unlimited;

    companion object {

        fun check(promotion: Promotion, historyOrders: List<TransferOrder>): Boolean {

            val today = LocalDate.now()
            val promotionHistory = historyOrders.filter { it.joinPromotionId == promotion.id }

            return when (promotion.period) {
                // 特定日期
                Sunday -> {
                    val sunday = today.with(DayOfWeek.SUNDAY)

                    sunday.dayOfWeek == DayOfWeek.SUNDAY
                            && this.checkPeriodMaxPromotion(promotion = promotion, history = promotionHistory, startDate = sunday, endDate = sunday)
                }
                WorkingDay -> {
                    val monday = today.with(DayOfWeek.MONDAY)
                    val friday = today.with(DayOfWeek.FRIDAY)

                    (today.dayOfWeek != DayOfWeek.SATURDAY && today.dayOfWeek == DayOfWeek.SUNDAY)
                            && this.checkPeriodMaxPromotion(promotion = promotion, history = promotionHistory, startDate = monday, endDate = friday)
                }
                Weekend -> {
                    val saturday = today.with(DayOfWeek.SATURDAY)
                    val sunday = today.with(DayOfWeek.SUNDAY)

                    (today.dayOfWeek == DayOfWeek.SATURDAY || today.dayOfWeek == DayOfWeek.SUNDAY)
                            && this.checkPeriodMaxPromotion(promotion = promotion, history = promotionHistory, startDate = saturday, endDate = sunday)
                }

                // 每天、周周、每月(一次)
                DailyOnce -> {
                    this.checkPeriodOnce(history = promotionHistory, startDate = today, endDate = today)
                }
                WeeklyOnce -> {
                    val monday = today.with(DayOfWeek.MONDAY)
                    val sunday = today.with(DayOfWeek.SUNDAY)
                    this.checkPeriodOnce(history = historyOrders, startDate = monday, endDate = sunday)
                }
                MonthlyOnce -> {
                    val firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth())
                    val lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())
                    this.checkPeriodOnce(history = historyOrders, startDate = firstDayOfMonth, endDate = lastDayOfMonth)
                }

                // 每天、周周、每月
                Daily -> {
                    this.checkPeriodMaxPromotion(promotion = promotion, history = promotionHistory, startDate = today, endDate = today)
                }
                Weekly -> {
                    val monday = today.with(DayOfWeek.MONDAY)
                    val sunday = today.with(DayOfWeek.SUNDAY)
                    this.checkPeriodMaxPromotion(promotion = promotion, history = promotionHistory, startDate = monday, endDate = sunday)
                }
                Monthly -> {
                    val firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth())
                    val lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())

                    this.checkPeriodMaxPromotion(promotion = promotion, history = historyOrders, startDate = firstDayOfMonth, endDate = lastDayOfMonth)
                }
                else -> true
            }
        }

        fun getOverPromotionAmount(promotion: Promotion, historyOrders: List<TransferOrder>): BigDecimal {

            val today = LocalDate.now()
            val promotionHistory = historyOrders.filter { it.joinPromotionId == promotion.id }

            val (startDate, endDate) = when (promotion.period) {
                // 特定日期
                Sunday -> {
                    val sunday = today.with(DayOfWeek.SUNDAY)
                    sunday to sunday
                }
                WorkingDay -> {
                    val monday = today.with(DayOfWeek.MONDAY)
                    val friday = today.with(DayOfWeek.FRIDAY)
                    monday to friday
                }
                Weekend -> {
                    val saturday = today.with(DayOfWeek.SATURDAY)
                    val sunday = today.with(DayOfWeek.SUNDAY)

                    saturday to sunday
                }

                // 每天、周周、每月(一次)
                DailyOnce -> today to today
                WeeklyOnce -> {
                    val monday = today.with(DayOfWeek.MONDAY)
                    val sunday = today.with(DayOfWeek.SUNDAY)

                    monday to sunday
                }
                MonthlyOnce -> {
                    val firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth())
                    val lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())

                    firstDayOfMonth to lastDayOfMonth
                }

                // 每天、周周、每月
                Daily -> today to today
                Weekly -> {
                    val monday = today.with(DayOfWeek.MONDAY)
                    val sunday = today.with(DayOfWeek.SUNDAY)
                    monday to sunday
                }
                Monthly -> {
                    val firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth())
                    val lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())

                    firstDayOfMonth to lastDayOfMonth
                }
                else -> today to today
            }

            return promotionHistory.filter { it.createdTime >= startDate.atStartOfDay() && it.createdTime <= endDate.atStartOfDay().plusDays(1) }
                    .sumByDouble { it.promotionAmount.toDouble() }
                    .let { promotion.periodMaxPromotion.minus(BigDecimal.valueOf(it)).setScale(2, 2) }
        }


        fun checkPeriodMaxPromotion(promotion: Promotion, history: List<TransferOrder>, startDate: LocalDate, endDate: LocalDate): Boolean {
            val totalHistoryPromotion = history.filter { startDate > it.createdTime.toLocalDate() && it.createdTime.toLocalDate() < endDate }
                    .sumByDouble { it.promotionAmount.toDouble() }
            return promotion.periodMaxPromotion.toDouble() > totalHistoryPromotion
        }

        fun checkPeriodOnce(history: List<TransferOrder>, startDate: LocalDate, endDate: LocalDate): Boolean {
            return history.firstOrNull { startDate >= it.createdTime.toLocalDate() && it.createdTime.toLocalDate() <= endDate } == null
        }

    }

}