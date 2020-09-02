package com.onepiece.gpgaming.games

open class HttpLogResponse<T>(

        // 请求地址
        val path: String,

        // Get or Post
        val method: String,

        // 请求参数
        val params: String,

        // 响应码
        val httpCode: String,

        // 返回数据
        val response: String,

        val data: T
)