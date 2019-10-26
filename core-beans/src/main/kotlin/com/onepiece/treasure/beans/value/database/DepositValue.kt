package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Bank
import com.onepiece.treasure.beans.enums.OrderState
import com.onepiece.treasure.beans.enums.DepositState
import java.math.BigDecimal
import java.time.LocalDateTime

data class DepositQuery(

        val clientId: Int,

        val startTime: LocalDateTime,

        val endTime: LocalDateTime,

        val orderId: String?,

        val memberId: Int?,

        val state: DepositState?
)

data class DepositCo(

        // 订单Id
        val orderId: String,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 存款人姓名
        val name: String,

        // 充值银行
        val bank: Bank,

        // 银行卡号
        val bankCardNumber: String,

        // 厅主银行卡Id
        val clientBankId: Int,

        // 厅主银行卡名称
        val clientBankName: String,

        // 厅主银行卡号
        val clientBankCardNumber: String,

        // 充值金额
        val money: BigDecimal,

        // 上传图片地址
        val imgPath: String
)


data class DepositUo(

        // 订单Id
        val orderId: String,

        // 流程Id 用于乐观锁
        val processId: String,

        // 充值状态
        val state: OrderState,

        // 备注
        val remarks: String?
)