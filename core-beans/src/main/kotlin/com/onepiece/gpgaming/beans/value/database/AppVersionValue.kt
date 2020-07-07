package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.LaunchMethod

sealed class AppVersionValue {

    data class AppVersionVo(

            val id: Int,

            // 启动方式
            val launch: LaunchMethod,

            // 上传地址
            val url: String,

            // 版本
            val version: String,

            // 内容
            val content: String,

            // 是否强制更新
            val constraint: Boolean

    )

}