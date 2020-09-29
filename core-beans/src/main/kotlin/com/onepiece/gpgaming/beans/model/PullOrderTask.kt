package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.U9RequestStatus
import java.time.LocalDateTime

data class PullOrderTask(
        // id
        val id: Int,

        // 线程号
        val nonce: String,

        // 业主Id
        val clientId: Int,

        // 平台
        val platform: Platform,

        // head信息
        val headers: String,

        // 请求地址
        val path: String,

        // 请求参数
        val param: String,

        // 表单数据
        val formParam: String,

        // 响应参数
        val response: String,

        // log 消息
        val logInfo: String,

        // 错误消息
        val message: String,

        // 执行类型
        val type: OrderTaskType,

        // 请求状态
        val status: U9RequestStatus,

        // 开始时间
        val startTime: LocalDateTime,

        // 结束时间
        val endTime: LocalDateTime
) {

    // 是否成功
    val ok: Boolean = status == U9RequestStatus.OK

    enum class OrderTaskType {

        API_REGISTER,

        API_BALANCE,

        API_TRANSFER,

        API_TRANSFER_CHECK,

        API_LAUNCH_GAME,

        API_QUERY_BET,

        API_QUERY_REPORT,

        API_PULL_BET,

        ORDER_MINUTE,

        ORDER_PRE_HOUR,


        MINUTE_13,

        HOUR_5,

    }
}