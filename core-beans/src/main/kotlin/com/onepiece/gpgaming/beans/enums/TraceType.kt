package com.onepiece.gpgaming.beans.enums

enum class TraceType {

    // 注册追踪
    Register,

    // 3天未登陆
    Login3Days,

    // 7天未登陆
    Login7Days,

    // 7天以上
    LoginNever,

    // 3天未充值
    Deposit3Days,

    // 7天未充值
    Deposit7Days,

    // 从未充值
    DepositNever

}