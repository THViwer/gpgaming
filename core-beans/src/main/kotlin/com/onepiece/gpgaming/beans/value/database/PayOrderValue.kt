package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.PayState
import com.onepiece.gpgaming.beans.enums.PayType
import java.math.BigDecimal
import java.time.LocalDate

sealed class PayOrderValue {

    data class PayOrderQuery(

            // 业主Id
            val clientId: Int,

            // 会员Id
            val memberId: Int?,

            // 会员Id列表
            val memberIds: List<Int>?,

            // 用户名
            val username: String?,

            // 平台
            val payType: PayType?,

            // 订单Id
            val orderId: String?,

            // 状态
            val state: PayState?,

            // 开始时间
            val startDate: LocalDate?,

            // 结束时间
            val endDate: LocalDate?,

            // 排序
            val sortBy: String = "created_time desc",

            // 当前记录数
            val current: Int,

            // 每页显示多少条数据
            val size: Int
    )

    data class PayOrderCo(

            // 业主Id
            val clientId: Int,

            // 会员Id
            val memberId: Int,

            // 用户名
            val username: String,

            // 订单Id
            val orderId: String,

            // 支付Id
            val payId: Int,

            // 支付平台
            val payType: PayType,

            // 支付银行
            val bank: Bank?,

            // 金额
            val amount: BigDecimal

    )

    data class ConstraintUo(

            // 订单Id
            val orderId: String,

            // 操作者Id
            val operatorId: Int,

            // 操作者用户名
            val operatorUsername: String,

            // 备注
            val remark: String
    )

    data class PayOrderMReport(

            // 业主Id
            val clientId: Int,

            // 会员Id
            val memberId: Int,

            // 总金额
            val totalAmount: BigDecimal,

            // 总数
            val count: Int
    )

    data class PayOrderCPReport(

            // 业主Id
            val clientId: Int,

            // 支付平台
            val payType: PayType,

            // 总金额
            val totalAmount: BigDecimal
    )

    data class PayOrderCReport(


            // 业主Id
            val clientId: Int,

            // 总金额
            val totalAmount: BigDecimal,

            // 总数
            val count: Int,

            // 三方充值人数
            val thirdPaySequence: Int
    )

    data class ThirdPaySummary(

            // 银行
            val bank: Bank,

            // 支付状态
            val state: PayState,

            // 总金额
            val totalAmount: BigDecimal
    )

}