package com.onepiece.treasure.beans.exceptions

import java.lang.RuntimeException

class LogicException(code: Int): RuntimeException()

//class BusinessException(code: Int): RuntimeException()

object OnePieceExceptionCode {

    // system error
    const val SYSTEM = "1000"

    const val DB_CHANGE = "1001"

    // 2000 verification error

    // 3000 auth check error

    // 4000 account error
    const val LOGIN_FAIL = "4001" // 用户名或密码错误
    const val PASSWORD_FAIL = "4002" // 密码错误

    // 5000 cash error


}