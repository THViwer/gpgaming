package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 下注订单
 * 分表规则: platform + clientId + memberId 获得hashCode值 取模8
 */
data class BetOrder(

        // id
        val id: Int,

        // 厅主
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 订单Id
        val orderId: String,

        // 平台
        val platform: Platform,

        // 下注金额
        val betAmount: BigDecimal,

        // 获得金额
        val winAmount: BigDecimal,

        // 标记已处理打码量
        val mark: Boolean,

        // 原始订单数据(json格式)
        val originData: String,

        // 下注时间
        val betTime: LocalDateTime,

        // 结算时间
        val settleTime: LocalDateTime,

        // 创建时间
        val createdTime: LocalDateTime,

        // 状态
        val status: Status

)