package com.onepiece.gpgaming.beans.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 主钱包
 */
data class Wallet(

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 余额
        val balance: BigDecimal,

        // 冻结金额
        val freezeBalance: BigDecimal,

        // 总充值金额
        val totalDepositBalance: BigDecimal,

        // 总转出次数
        val totalTransferOutFrequency: Int,

        // 总转入次数
        val totalTransferInFrequency: Int,

        // 总取款金额
        val totalWithdrawBalance: BigDecimal,

        // 总优惠金额
        val totalGiftBalance: BigDecimal,

        // 总存款次数
        val totalDepositFrequency: Int,

        // 总提款次数
        val totalWithdrawFrequency: Int,

        // 进程Id
        val processId: String,

        // 创建时间
        val createdTime: LocalDateTime
)