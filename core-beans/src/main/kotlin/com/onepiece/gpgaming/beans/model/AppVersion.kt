package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.LaunchMethod
import java.time.LocalDateTime

data class AppVersion(

        val id: Int,

        // 业主Id
        val mainClientId: Int,

        // 启动方式
        val launch: LaunchMethod,

        // 上传地址
        val url: String,

        // 版本
        val version: String,

        // 内容
        val content: String,

        // 是否强制更新
        val constraint: Boolean,

        // 创建时间
        val createdTime: LocalDateTime

)