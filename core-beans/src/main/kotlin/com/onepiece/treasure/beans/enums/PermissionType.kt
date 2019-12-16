package com.onepiece.treasure.beans.enums

enum class PermissionType(
        val parentId: Int,
        val resourceId: Int,
        val ename: String,
        val cname: String
) {

    // 用户管理
    USER_MANAGE(-1, 1000,"用户管理", "用户管理"),
    USER_MEMBER(1000, 1100,"会员管理", "会员管理"),
    USER_WAITER(1000, 1200,"客服管理", "客服管理"),
    USER_MEMBER_LEVEL(1000, 1300,"层级管理", "层级管理"),

    // 现金系统
    CASH(-1, 2000, "现金系统", "现金系统"),
    CASH_DEPOSIT_CHECK(2000, 2100, "充值审核", "充值审核"),
    CASH_WITHDRAW_CHECK( 2000, 2200, "提款审核", "提款审核"),
    CASH_DEPOSIT_HISTORY(2000, 2300, "充值审核", "充值审核"),
    CASH_WITHDRAW_HISTORY(2000, 2400, "提款审核", "提款审核"),
    CASH_Artificial(2000, 2500, "人工提存", "人工提存"),

    // 注单管理
    BET_MANAGE(-1, 3000, "注单管理", "注单管理"),

    // 官站管理
    WEBSITE_MANAGE(-1, 4000, "网站管理", "网站管理"),
    WEBSITE_ANNOUNCEMENT(4000, 4100, "公告管理", "公告管理"),
    WEBSITE_BANNER(4000, 4200, "Banner", "Banner"),
    WEBSITE_PROMOTION( 4000, 4300, "优惠活动", "优惠活动"),
    WEBSITE_CONTACT(4000, 4400, "联系我们", "联系我们"),

    // 报表
    REPORT_MANAGE(-1, 5000, "报表", "报表"),
    REPORT_SYSTEM(5000, 5100, "系统报表", "系统报表"),
    REPORT_MEMBER(5000, 5200, "会员报表", "会员报表"),
    REPORT_PROMOTION(5000, 5300, "优惠报表", "优惠报表")


}