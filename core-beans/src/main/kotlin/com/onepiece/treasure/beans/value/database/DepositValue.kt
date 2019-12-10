package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Bank
import com.onepiece.treasure.beans.enums.DepositChannel
import com.onepiece.treasure.beans.enums.OrderState
import com.onepiece.treasure.beans.enums.DepositState
import java.math.BigDecimal
import java.time.LocalDateTime

data class DepositQuery(

        val clientId: Int,

        val startTime: LocalDateTime?,

        val endTime: LocalDateTime?,

        val orderId: String?,

        val lockWaiterId: Int?,

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

        // 用户名
        val username: String,

        // 会员银行卡Id
        val memberBankId: Int,

        // 存款人姓名
        val memberName: String,

        // 充值银行
        val memberBank: Bank,

        // 银行卡号
        val memberBankCardNumber: String,

        // 厅主银行卡Id
        val clientBankId: Int,

        // 厅主银行卡名称
        val clientBankName: String,

        // 厅主银行卡号
        val clientBankCardNumber: String,

        // 转账时间
        val depositTime: LocalDateTime,

        // 转账通道
        val channel: DepositChannel,

        // 充值金额
        val money: BigDecimal,

        // 上传图片地址
        val imgPath: String?
)


data class DepositUo(

        val clientId: Int,

        // 订单Id
        val orderId: String,

        // 流程Id 用于乐观锁
        val processId: String,

        // 充值状态
        val state: DepositState,

        // 备注
        val remarks: String?,

        // 锁定客服Id
        val lockWaiterId: Int
)

data class DepositLockUo(

        // 厅主Id
        val clientId: Int,

        // 订单Id
        val orderId: String,

        // 流程Id
        val processId: String,

        // 锁定人员
        val lockWaiterId: Int,

        // 锁定人名称
        val lockWaiterName: String
)