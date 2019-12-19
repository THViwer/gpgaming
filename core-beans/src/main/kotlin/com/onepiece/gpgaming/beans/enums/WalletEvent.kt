package com.onepiece.gpgaming.beans.enums

enum class WalletEvent {

    // 充值
    DEPOSIT,

    // 冻结
    FREEZE,

    // 取款
    WITHDRAW,

    // 取款失败
    WITHDRAW_FAIL,

    // 转账
    TRANSFER_IN,

    // 转张回滚
    TRANSFER_IN_ROLLBACK,

    // 转出
    TRANSFER_OUT,

    // 转出回滚
    TRANSFER_OUT_ROLLBACK,

    // 人工出入款
    Artificial

    // 补偿
//    REPARATION

    // 投注
//    BET


}