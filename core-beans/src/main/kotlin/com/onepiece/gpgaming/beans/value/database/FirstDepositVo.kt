package com.onepiece.gpgaming.beans.value.database

import java.math.BigDecimal

data class FirstDepositVo(

        // client id
        val clientId: Int,

        // 首充人次
        val firstDepositFrequency: Int,

        // 首充金额
        val totalFirstDeposit: BigDecimal
) {

    companion object {

        fun merge(d1: List<FirstDepositVo>, d2: List<FirstDepositVo>): List<FirstDepositVo> {
            return d1.plus(d2)
                    .groupBy { it.clientId }
                    .map {
                        val (_, _list)  = it

                            val firstDepositFrequency = _list.sumBy { a -> a.firstDepositFrequency }
                            val totalFirstDeposit = _list.sumByDouble { b -> b.totalFirstDeposit.toDouble() }.toBigDecimal().setScale(2, 2)

                        _list.first().copy(firstDepositFrequency = firstDepositFrequency, totalFirstDeposit = totalFirstDeposit)

                    }
        }

    }

}