package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Role
import java.math.BigDecimal


data class ArtificialOrderQuery(

        val clientId: Int,

        val operatorRole: Role? = null,

        val memberId: Int? = null,

        val waiterId: Int? = null,

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

        // 会员用户名
        val username: String,

        // 操作人Id
        val operatorId: Int,

        // 操作者用户名
        val operatorUsername: String,

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