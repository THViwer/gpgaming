package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDate
import java.time.LocalDateTime

data class TaskTimer(

        // id
        val id: Int,

        // 日期
        val day: LocalDate,

        // 状态
        val state: TaskTimerState,

        // 类型
        val type: TaskTimerType,

        // 创建时间
        val createdTime: LocalDateTime,

        // 更新时间
        val updatedTime: LocalDateTime,

        // 数据状态
        val status: Status

)

enum class TaskTimerType {

    // 会员平台日报表
    MemberPlatformDaily,

    // 会员日报表
    MemberDaily,

    // 业主平台日报表
    ClientPlatformDaily,

    // 会员日报表
    ClientDaily,

    // 优惠平台日报表
    PromotionPlatformDaily,

    // 优惠日报表
    PromotionDaily,

    // 代理日报表
    AgentDaily,

    // 代理月报表
    AgentMonth,

    // 电销日报表
    SaleDaily,

    // 电销月报表
    SaleMonth,

    // 营销日报表
    MarketDaily

}

enum class TaskTimerState {

    // 进行中
    Process,

    // 结束
    Done,

    // 失败
    Fail
}