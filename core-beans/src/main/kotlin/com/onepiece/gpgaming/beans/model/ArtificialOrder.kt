package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 人工提存订单
 */
data class ArtificialOrder(

        // id
        val id: Int,

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

        // 操作后余额
        val balance: BigDecimal,

        // 操作之前余额
        val beforeBalance: BigDecimal,

        // 备注
        val remarks: String,

        // 创建时间
        val createdTime: LocalDateTime,

        // 状态
        val status: Status

)