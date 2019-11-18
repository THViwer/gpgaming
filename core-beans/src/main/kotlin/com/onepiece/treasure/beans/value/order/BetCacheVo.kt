package com.onepiece.treasure.beans.value.order

import com.onepiece.treasure.beans.enums.Platform
import java.math.BigDecimal

data class BetCacheVo(
        val memberId: Int,
        val platform: Platform,
        val bet: BigDecimal,
        val win: BigDecimal
) {

    companion object {

//        fun spllitBetCache(v: String): BetCacheVo {
//
//            val array = v.split("_")
//            val memberId = array[0].toInt()
//            val bet = array[1].toBigDecimal()
//            return BetCacheVo(memberId = memberId, bet = bet)
//        }

//        fun of(memberId: String, bet: BigDecimal): String {
//            return "${memberId}_$bet"
//        }
    }

//    fun toParams(): String {
//        return "${memberId}_$bet"
//    }



}