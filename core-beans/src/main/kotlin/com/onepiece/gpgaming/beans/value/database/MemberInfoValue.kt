package com.onepiece.gpgaming.beans.value.database

import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

sealed class MemberInfoValue {

    data class MemberInfoCo(

            // 基础信息
            val bossId: Int,

            val clientId: Int,

            val agentId: Int,

            val saleId: Int,

            val memberId: Int,

            val username: String,

            val registerTime: LocalDateTime = LocalDateTime.now()
    )

    data class MemberInfoUo(

            val memberId: Int,

            val saleId: Int? = null,

            // 充值金额
            val deposit: BigDecimal = BigDecimal.ZERO,

            // 充值时间
            val depositTime: LocalDateTime? = null,

            val depositCount: Int = 0,


            // 取款金额
            val withdraw: BigDecimal = BigDecimal.ZERO,

            // 取款时间
            val withdrawTime: LocalDateTime? = null,

            val withdrawCount: Int = 0,


            // 登陆时间
            val lastLoginTime: LocalDateTime? = null,

            // 登陆次数
            val loginCount: Int = 0,

            // 电销信息
            val lastSaleTime: LocalDateTime? = null,

            val saleCount: Int = 0,

            val nextCallTime: LocalDateTime? = null

    ) {

        companion object {

            fun ofUpdateSale(memberId: Int, saleId: Int): MemberInfoUo {
                return MemberInfoUo(memberId = memberId, saleId = saleId)
            }

            fun ofLogin(memberId: Int): MemberInfoUo {
                return MemberInfoUo(memberId = memberId, lastLoginTime = LocalDateTime.now(), loginCount = 1)
            }

            fun ofDeposit(memberId: Int, amount: BigDecimal): MemberInfoUo {
                return MemberInfoUo(memberId = memberId, deposit = amount, depositTime = LocalDateTime.now(), depositCount = 1)
            }

            fun ofWithdraw(memberId: Int, amount: BigDecimal): MemberInfoUo {
                return MemberInfoUo(memberId = memberId, withdraw = amount, withdrawTime = LocalDateTime.now(), withdrawCount = 1)
            }

            fun ofSale(memberId: Int, nextCallTime: LocalDateTime?): MemberInfoUo {
                return MemberInfoUo(memberId = memberId, lastSaleTime = LocalDateTime.now(), saleCount = 1, nextCallTime = nextCallTime)
            }

        }

    }

    data class MemberInfoQuery(

            val bossId: Int,

            val clientId: Int,

            val saleId: Int? = null,

            val memberId: Int? = null,

            val username: String? = null,

            // 最小总充值金额
            val totalDepositMin: BigDecimal? = null,

            // 最大总充值金额
            val totalDepositMax: BigDecimal? = null,

            // 最后充值时间最小
            val lastDepositTimeMin: LocalDate? = null,

            // 最后充值时间最大
            val lastDepositTimeMax: LocalDate? = null,

            // 充值次数最小
            val totalDepositCountMin: Int? = null,

            // 充值次数最大
            val totalDepositCountMax: Int? = null,



            // 注册时间最小
            val registerTimeMin: LocalDate? = null,

            // 注册时间最大
            val registerTimeMax: LocalDate? = null,

            // 最后登陆时间最小
            val lastLoginTimeMin: LocalDate? = null,

            // 最后登陆时间最大
            val lastLoginTimeMax: LocalDate? = null,

            // 登陆次数最小
            val loginCountMin: Int? = null,

            //登陆次数最大
            val loginCountMax: Int? = null,

            // 最后电销时间最小
            val lastSaleTimeMin: LocalDate? = null,

            // 最后电销时间最大
            val lastSaleTimeMax: LocalDate? = null,

            // 电销总数最小
            val saleCountMin: Int? = null,

            // 电销总数最大
            val saleCountMax: Int? = null,

            // 排序字段
            val sortBy: String = "member_id desc"
    )

    data class MemberInfoVo(

            // 电销Id
            val saleId: Int,

            // 电销用户名
            val saleUsername :String,

            // 用户Id
            val memberId: Int,

            // 用户名
            val username: String,

            // 电话
            val phone: String,

            // 姓名
            val name: String,

            // 充值信息
            val totalDeposit: BigDecimal,

            val lastDepositTime: LocalDateTime?,

            val totalDepositCount: Int,


            // 取款信息
            val totalWithdraw: BigDecimal,

            val lastWithdrawTime: LocalDateTime?,

            val totalWithdrawCount: Int,

            // 登陆信息

            val registerTime: LocalDateTime,

            val lastLoginTime: LocalDateTime?,

            val loginCount: Int,

            // 电销信息
            val lastSaleTime: LocalDateTime?,

            val saleCount: Int,

            // 下一次电话时间
            val nextCallTime: LocalDateTime?

    ) {

//        val nextCall: String
//            get() {
//                if (nextCallTime == null) return "-"
//
//                val duration = Duration.between(nextCallTime, LocalDateTime.now())
//                when {
//                    duration.toDays() > 1 -> "more than 1 days"
//                    duration.toHours() > 1 -> "${duration.toHours()} hour"
//                    duration.toMinutes() >
//                }
//
//            }

        val notLoginDays: Int
            get() {
                return lastLoginTime?.let {
                    Duration.between(it, LocalDateTime.now()).toDays().toInt()
                } ?: -1
            }

        val notDepositDays: Int
            get() {
                return lastDepositTime?.let {
                    Duration.between(it, LocalDateTime.now()).toDays().toInt()
                } ?: -1
            }

        val notSaleDays: Int
            get() {
                return lastSaleTime?.let {
                    Duration.between(it, LocalDateTime.now()).toDays().toInt()
                } ?: -1
            }
    }
}