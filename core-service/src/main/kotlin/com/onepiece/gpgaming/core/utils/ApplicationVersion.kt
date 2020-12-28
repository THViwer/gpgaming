package com.onepiece.gpgaming.core.utils

import java.math.BigDecimal

object ApplicationVersion {

    // 介绍人推荐注册佣金
    val INTRODUCE_REGISTER_COMMISSION: BigDecimal = BigDecimal.valueOf(2)

    // 介紹人充值金額滿50元才会有送资金
    const val INTRODUCE_DEPOSIT_NEED_AMOUNT = 50

    // 介绍人推荐注册并充值满50元佣金
    val INTRODUCE_REGISTER_DEPOSIT_COMMISSION: BigDecimal = BigDecimal.valueOf(5)

    fun checkIsNewVersion(clientId: Int): Boolean {
        return clientId == 10001 || clientId == 100002
    }

}