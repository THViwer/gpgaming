package com.onepiece.gpgaming.mr.selenium

object PayErrorCode {

    // 成功
    const val SUCCESSFUL = 0

    // 订单已提早结束
    const val SUCCESSFUL_AND_DONE = 1

    //错误码基线
    const val SYSTEM_LEVEL = 999

    // 系统异常
    const val SYSTEM_FAIL = 1000

    // 登陆失败-用户名密码不匹配
    const val LOGIN_FAIL = 1001

    // 余额不足
    const val BALANCE_FAIL = 1002

    // 验证码失败
    const val VERIFY_CODE_FAIL = 1003

    // 转账失败
    const val TRANSFER_FAIL = 1004

    // 该银行正在执行中
    const val BANK_RUN_EXIST = 1005

    // 超时
    const val TIMEOUT = 1006

    // 订单不存在
    const val ORDER_NOT_EXIST  = 1007

    // 未匹配到设备
    const val NOT_MATCH_DEVICE = 1008

    // 重复操作
    const val REPEAT_OPERATION = 1009

    //充值金额错误
    const val AMOUNT_ERROR = 1010

    // 订单支付失败
    const val ORDER_FAILED = 1011

    //RHB OTP验证失败
    const val VERIFY_OTP_FAIL = 1012

    //MBB用户名错误
    const val INVALID_USERNAME = 1013

    //MBB密码格式错误
    const val INVALID_PASSWORD = 1014

    //HLB银行拒绝发起转账,PBB银行拒绝发起转账
    //HLB:Sorry, we cannot process your request. Please try again later
    //PBB:Error Code : (.015.2C.575069532)
    const val BANK_REFUSE = 1015

    //定位元素失败
    const val POSITION_ERROR = 1016

    //系统异常 无orderId对应的chrome
    const val MATCH_CHROME_FAIL = 1017

    //用户账户下无银行卡信息
    const val USER_NO_BANKCARD = 1018

    //用户未APP验证
    const val USER_NO_OPERATE = 1019

    //未匹配到相应的银行卡
    const val NOT_MATCH_BANK = 1020

    //无效银行类型
    const val INVALID_BANK = 1021

    //超出用户转账限额
    const val LIMIT_EXCEEDED = 1022

    //银行系统维护
    const val SYSTEM_DOWNTIME = 1023

    //PBB登录后安全问题
    const val SYSTEM_SECURITY_QUESTION = 1024

    //网络异常
    const val NETWORK_ERROR = 1025

    //网络访问被限制
    const val ACCESS_DENIED = 1026
}