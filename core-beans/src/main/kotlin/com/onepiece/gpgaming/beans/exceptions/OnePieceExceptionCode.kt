package com.onepiece.gpgaming.beans.exceptions

//class LogicException(code: String): RuntimeException()

//class BusinessException(code: Int): RuntimeException()

object OnePieceExceptionCode {

    // system error
    const val SYSTEM = "1000"
    const val DB_CHANGE_FAIL = "1001"
    const val DATA_FAIL = "1002" // 数据异常
    const val LANGUAGE_CONFIG_FAIL = "1003" // 语言配置错误
    const val QUERY_COUNT_TOO_SMALL = "1004" // 请增加查询条件
    const val PROMOTION_JSON_DATA_FAIL = "1005" //优惠活动json格式错误
    const val ILLEGAL_OPERATION = "1006" // 非法操作
    const val IP_ILLEGAL = "1008" // ip非法
    const val DATA_EXIST = "1009" // 数据已存在
    const val SMS_SMS_COUNT_MORE_THAN_500 = "1010" // 短信发布数量不能超过1000

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
    const val DEFAULT_LEVEL_FAIL = "4007" // 默认层级不可修改
    const val MOVE_LEVEL_COUNT_ISZERO = "4008" // 移动人数为0
    const val MOVE_LEVEL_COUNT_ISMAX = "4009" // 移动人数为0
    const val PLATFORM_DATA_FAIL = "4010" // 平台返回数据错误
    const val MEMBER_BANK_EXIST = "4011" // 用户该银行已有银行卡
    const val AGENT_PROCESS = "4012" // 代理审核中
    const val PHONE_NEVER_REGISTER = "4013" // 该手机 未注册
    const val SMS_CODE_ERROR = "4014" // 验证码失败
    const val SMS_CODE_TIMEOUT = "4015" // 验证码超时

    // 5000 cash error
    const val ORDER_EXPIRED = "5001" // 订单操作过期
    const val TRANSFER_OUT_BET_FAIL = "5002" // 不可转出 打码量不足
    const val BALANCE_SHORT_FAIL = "5003" // 余额不足
    const val SAFETY_PASSWORD_CHECK_FAIL = "5004" // 取款密码错误
    const val BALANCE_NOT_WORTH = "5005" // 余额不足
    const val CENTER_TO_PLATFORM_FAIl = "5006" // 中心转平台 不满足优惠活动转入
    const val PLATFORM_TO_CENTER_FAIL = "5007" // 平台转中心 不满足优惠活动转出
    const val PLATFORM_HAS_BALANCE_PROMOTION_FAIL = "5008" // 平台钱包有余额 不可参加活动
    const val PROMOTION_EXPIRED = "5009" // 优惠活动过期
    const val EARNESTBALANCE_OVER = "5010" // 厅主保证金不足
    const val TRANSFER_FAILED = "5011" // 转账失败
    const val BANK_CARD_ERROR = "5012" // 银行卡错误 不为数字或长度不够
    const val TRANSFER_TIME_FAST = "5013" // 转账时间过快
    const val PROMOTION_CODE_EXIST = "5014" // 优惠码已存在
    const val NEVER_DEPOSIT = "5015" // 从未充值 不能出款


    // 60000 platform method error
    const val PLATFORM_METHOD_FAIL = "6001" // 平台方式错误
    const val PLATFORM_TRANSFER_ORDERID_EXIST = "6002" // 转账订单已存在
    const val PLATFORM_AEGIS = "6003" // 平台维护
    const val PLATFORM_REQUEST_ERROR = "6004" // 平台调用接口错误
    const val PLATFORM_MAINTAIN = "6005" // 游戏平台维护

}