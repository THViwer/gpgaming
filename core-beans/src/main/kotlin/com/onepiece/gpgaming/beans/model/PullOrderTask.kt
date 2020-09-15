package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Platform
import java.time.LocalDateTime

data class PullOrderTask(
        // id
        val id: Int,

        // 业主Id
        val clientId: Int,

        // 平台
        val platform: Platform,

        // 请求地址
        val path: String,

        // 请求参数
        val param: String,

        // 响应参数
        val response: String,

        // 错误消息
        val message: String,

        // 执行类型
        val type: OrderTaskType,

        // 是否成功
        val ok: Boolean,

        // 开始时间
        val startTime: LocalDateTime,

        // 结束时间
        val endTime: LocalDateTime
) {

    enum class OrderTaskType {

        MINUTE,

        MINUTE_13,

        HOUR_5,

    }
}