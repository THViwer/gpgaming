package com.onepiece.gpgaming.beans.enums

enum class PermissionType(
        val parentId: String,
        val resourceId: String,
        val ename: String,
        val cname: String
) {

    // 用户管理
    USER_MANAGE("-1", "1000","用户管理", "用户管理"),
    USER_MEMBER("1000", "1100","会员管理", "会员管理"),
    USER_WAITER("1000", "1200","客服管理", "客服管理"),
    USER_MEMBER_LEVEL("1000", "1300","层级管理", "层级管理"),
    USER_FOLLOW("1000", "1400","会员追踪", "会员追踪"),
    USER_PRIVACY("1000", "1500", "会员隐私", "会员隐私"),

    // 现金系统
    CASH("-1", "2000", "现金系统", "现金系统"),
    CASH_DEPOSIT_CHECK("2000", "2100", "充值审核", "充值审核"),
    CASH_WITHDRAW_CHECK( "2000", "2200", "提款审核", "提款审核"),
    CASH_DEPOSIT_HISTORY("2000", "2300", "充值审核", "充值审核"),
    CASH_WITHDRAW_HISTORY("2000", "2400", "提款审核", "提款审核"),
    CASH_ARTIFICIAL("2000", "2500", "人工提存", "人工提存"),
    CASH_BANK_MANAGER("2000", "2600", "银行卡管理", "银行卡管理"),
    CASH_TRANSFER_ORDER("2000", "2700", "转账单订单", "转账单订单"),
    CASH_THIRD_PAY_SETTING("2000", "2800", "三方支付平台设置", "三方支付平台设置"), // 特定 只有super_admin才有

    // 注单管理
    BET_MANAGE("-1", "3000", "注单管理", "注单管理"),

    // 官站管理
    WEBSITE_MANAGE("-1", "4000", "网站管理", "网站管理"),
    WEBSITE_ANNOUNCEMENT("4000", "4100", "公告管理", "公告管理"),
    WEBSITE_BANNER("4000", "4200", "Banner", "Banner"),
    WEBSITE_PROMOTION( "4000", "4300", "优惠活动", "优惠活动"),
    WEBSITE_HOT_GAME("4000", "4400", "热门游戏", "联系我们"),
    WEBSITE_INDEX_VIDEO("4000", "4500", "首页视频", "首页视频"),
    WEBSITE_INDEX_SPORT("4000", "4600", "首页体育", "首页体育"),
    WEBSITE_INDEX_LIVE("4000", "4700", "首页真人", "首页真人"),
    WEBSITE_SEO("4000", "4800", "搜索优化", "搜索优化"),

    // 报表
    REPORT_MANAGE("-1", "5000", "报表", "报表"),
    REPORT_SYSTEM("5000", "5100", "系统报表", "系统报表"),
    REPORT_MEMBER("5000", "5200", "会员报表", "会员报表"),
    REPORT_MEMBER_EXCEL("5200", "5201", "会员报表导出", "会员报表导出"),

    REPORT_PROMOTION("5000", "5300", "优惠报表", "优惠报表"),
    REPORT_ANALYSIS("5000", "5400", "会员分析", "会员分析"),

    // 代理管理
    AGENT_MANAGER("-1", "6000", "代理", "代理"),

    // 电销系统
    SALE_MANAGER("-1", "7000", "电销", "电销"),

    // 营销
    MARKET_MANAGE("-1", "8000", "营销", "营销")


    ;

}