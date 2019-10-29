package com.onepiece.treasure.beans.exceptions

import java.lang.RuntimeException

class LogicException(code: String): RuntimeException()

//class BusinessException(code: Int): RuntimeException()

object OnePieceExceptionCode {

    // system error
    const val SYSTEM = "1000"

    const val DB_CHANGE_FAIL = "1001"

    // 2000 verification error

    // 3000 auth check error
    const val AUTHORITY_FAIL = "3001"

    // 4000 account error
    const val LOGIN_FAIL = "4001" // 用户名或密码错误
    const val PASSWORD_FAIL = "4002" // 密码错误
    const val USERNAME_EXISTENCE = "4003" // 用户名已存在
    const val USER_STOP = "4004" // 用户被停用
    const val SAFETY_PASSWORD_FAIL = "4005" // 安全码错误
    const val PLATFORM_MEMBER_REGISTER_FAIL = "4006" // 平台会员注册失败

    // 5000 cash error
    const val ORDER_EXPIRED = "5001" // 订单操作过期
    const val TRANSFER_OUT_BET_FAIL = "5002" // 不可转出 打码量不足
    const val BALANCE_SHORT_FAIL = "5003" // 余额不足
    const val SAFETY_PASSWORD_CHECK_FAIL = "5004" // 取款密码错误

    // 60000 platform method error
    const val PLATFORM_METHOD_FAIL = "6001" // 平台方式错误
    const val PLATFORM_TRANSFER_ORDERID_EXIST = "6002" // 转账订单已存在

}