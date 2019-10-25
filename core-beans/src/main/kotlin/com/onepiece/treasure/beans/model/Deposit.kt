package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Banks
import com.onepiece.treasure.beans.enums.DepositState
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 充值订单
 */
data class Deposit(

        // id
        val id: Int,

        // orderId
        val orderId: String,

        // 流程Id 用于乐观锁
        val processId: String,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 充值银行
        val bank: Banks,

        // 银行卡号
        val bankCardNumber: String,

        // 充值金额
        val money: BigDecimal,

        // 上传图片地址
        val imgPath: String,

        // 充值状态
        val state: DepositState,

        // 备注
        val remarks: String?,

        // 创建时间
        val createdTime: LocalDateTime,

        // 订单关闭时间
        val endTime: LocalDateTime?
)