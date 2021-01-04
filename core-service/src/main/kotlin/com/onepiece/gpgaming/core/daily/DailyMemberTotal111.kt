//package com.onepiece.gpgaming.core.daily
//
//import com.onepiece.gpgaming.beans.enums.Platform
//import com.onepiece.gpgaming.beans.model.MemberDailyReport
//import com.onepiece.gpgaming.utils.RedisService
//import org.springframework.stereotype.Component
//import java.math.BigDecimal
//import java.time.LocalDate
//
//@Component
//class DailyMemberTotal(
//        private val redisService: RedisService
//) {
//
//    companion object {
//        fun getMemberTodayDetailRedisKey(memberId: Int): String {
//            return "M:T:D:${memberId}:${LocalDate.now()}"
//        }
//
//        fun getMemberLockKey(memberId: Int): String {
//            return "M:L:K:$memberId"
//        }
//    }
//
//    fun tryLock(memberId: Int, function: (MemberDailyDetail) -> MemberDailyDetail) {
//
//        val lockKey = getMemberLockKey(memberId = memberId)
//        val redisKey = getMemberTodayDetailRedisKey(memberId = memberId)
//
//        redisService.lock(lockKey, error = {
//            tryLock(memberId, function)
//        }, function = {
//            val detail = getMemberTodayDetail(memberId = memberId)
//            val d2 = function(detail)
//            redisService.put(redisKey, d2)
//        })
//    }
//
//    fun unlock(memberId: Int) {
//
//    }
//
//    fun getMemberTodayDetail(memberId: Int): MemberDailyDetail {
//        val redisKey = getMemberTodayDetailRedisKey(memberId = memberId)
//        return redisService.get(redisKey, MemberDailyDetail::class.java)
//                ?: MemberDailyDetail(memberId = memberId)
//    }
//
//    fun addDeposit(memberId: Int, amount: BigDecimal) {
//        this.tryLock(memberId = memberId) { detail ->
//            val totalDeposit = amount.plus(detail.totalDeposit)
//            val totalDepositFrequency = detail.totalDepositFrequency.plus(1)
//            detail.copy(totalDeposit = totalDeposit, totalDepositFrequency = totalDepositFrequency)
//        }
//    }
//
//    fun addWithdraw(memberId: Int, amount: BigDecimal) {
//        this.tryLock(memberId = memberId) { detail ->
//            val totalWithdraw = amount.plus(detail.totalWithdraw)
//            val totalWithdrawFrequency = detail.totalWithdrawFrequency.plus(1)
//            detail.copy(totalWithdraw = totalWithdraw, totalWithdrawFrequency = totalWithdrawFrequency)
//        }
//    }
//
//    /**
//     *  // 平台
//    val platform: Platform,
//
//    // 下注
//    val bet: BigDecimal = BigDecimal.ZERO,
//
//    // 有效投注
//    val validBet: BigDecimal = BigDecimal.ZERO,
//
//    // 派彩 应该改为payout
//    val payout: BigDecimal = BigDecimal.ZERO,
//
//    // 反水
//    val rebate: BigDecimal = BigDecimal.ZERO,
//
//    // 已废弃
//    @JsonProperty("mwin")
//    val _mwin: BigDecimal = BigDecimal.ZERO,
//
//    // 必要打码
//    val requirementBet: BigDecimal = BigDecimal.ZERO,
//
//    // 反水比例
//    val rebateScale: BigDecimal = BigDecimal.ZERO
//     */
//
//    fun addTransfer(memberId: Int, from: Platform, to: Platform, amount: BigDecimal, promotion: BigDecimal) {
//        this.tryLock(memberId = memberId) { detail ->
//            val platform = if (from == Platform.Center) to else from
//
//            val settle = detail.settles.firstOrNull { it.platform == platform } ?: MemberDailyReport.PlatformSettle(platform = platform)
//
//            val (transferIn, transferOut) = if (from == Platform.Center) {
//                BigDecimal.ZERO to amount
//            } else {
//                amount to BigDecimal.ZERO
//            }
//            val totalIn = settle.totalIn.plus(transferIn)
//            val totalOut = settle.totalOut.plus(transferOut)
//            val settleNew = settle.copy(totalIn = totalIn, totalOut = totalOut)
//
//            val settles = detail.settles.map {
//                if (it.platform == platform) settleNew else it
//            }
//
//            val totalPromotion = detail.totalPromotion.plus(promotion)
//
//            detail.copy(totalPromotion = totalPromotion, settles = settles)
//        }
//    }
//
//    fun addBet(memberId: Int, platform: Platform, bet: BigDecimal, validBet: BigDecimal, payout: BigDecimal) {
//        this.tryLock(memberId = memberId) { detail ->
//
//            val settle = detail.settles.firstOrNull { it.platform == platform } ?: MemberDailyReport.PlatformSettle(platform = platform)
//
//            val settleBet = settle.bet.plus(bet)
//            val settleValidBet = settle.validBet.plus(validBet)
//            val settlePayout = settle.payout.plus(payout)
//            val settleNew = settle.copy(bet = settleBet, payout = settlePayout, validBet = settleValidBet)
//            val settles = detail.settles.map {
//                if (it.platform == platform) settleNew else it
//            }
//
//            val totalBet = detail.totalBet.plus(bet)
//            val
//
//            detail.copy(totalPromotion = totalPromotion, settles = settles)
//        }
//    }
//
//
//}