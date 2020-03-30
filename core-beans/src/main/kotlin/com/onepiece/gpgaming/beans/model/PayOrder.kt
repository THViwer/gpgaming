package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.PayState
import com.onepiece.gpgaming.beans.enums.PayType
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime

data class PayOrder (

        // id
        val id: Int,

        // 业主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 用户名s
        val username: String,

        // 金额
        val amount: BigDecimal,

        // 订单Id
        val orderId: String,

        // 第三方订单Id
        val thirdOrderId: String?,

        // 支付平台
        val payType: PayType,

        // 操作者Id
        val operatorId: Int?,

        // 操作者用户名
        val operatorUsername: String?,

        // 备注
        val remark: String?,

        // 状态
        val state: PayState,

        // 创建时间
        val createdTime: LocalDateTime,

        // 更新时间
        val updatedTime: LocalDateTime


)