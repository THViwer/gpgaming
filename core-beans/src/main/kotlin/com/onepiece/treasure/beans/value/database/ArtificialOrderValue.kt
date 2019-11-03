package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Role
import java.math.BigDecimal


data class ArtificialOrderQuery(

        val clientId: Int,

        val operatorRole: Role,

        val memberId: Int? = null,

        val current: Int,

        val size: Int

)

data class ArtificialOrderCo(

        // 订单Id
        val orderId: String,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 操作人Id
        val operatorId: Int,

        // 操作角色
        val operatorRole: Role,

        // 操作金额
        val money: BigDecimal,

        // 备注
        val remarks: String,

        // 余额
        val balance: BigDecimal = BigDecimal.ZERO

) {

    // 操作之前余额
    val beforeBalance: BigDecimal
        get() = balance.subtract(money)


}