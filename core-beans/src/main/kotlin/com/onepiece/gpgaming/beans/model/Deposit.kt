package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.DepositChannel
import com.onepiece.gpgaming.beans.enums.DepositState
import com.onepiece.gpgaming.beans.enums.Status
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

        // 会员登陆名
        val username: String,

        // 会员Id
        val memberId: Int,

        // 会员银行卡Id
        val memberBankId: Int,

        // 充值银行
        val memberBank: Bank,

        // 银行卡号
        val memberBankCardNumber: String,

        // 存款人姓名
        val memberName: String,

        // 厅主银行卡Id
        val clientBankId: Int,

        // 厅主银行
        val clientBank: Bank,

        // 厅主银行卡号
        val clientBankCardNumber: String,

        // 厅主银行卡姓名
        val clientBankName: String,

        // 充值金额
        val money: BigDecimal,

        // 是否是首充
        val firstDeposit: Boolean,

        // 转账时间
        val depositTime: LocalDateTime,

        // 转账通道
        val channel: DepositChannel,

        // 上传图片地址
        val imgPath: String?,

        // 充值状态
        val state: DepositState,

        // 备注
        val remarks: String?,

        // 锁定人员Id
        val lockWaiterId: Int?,

        // 锁定人员名称
        val lockWaiterName: String?,

        // 创建时间
        val createdTime: LocalDateTime,

        // 订单关闭时间
        val endTime: LocalDateTime?,

        // 状态
        val status: Status
)