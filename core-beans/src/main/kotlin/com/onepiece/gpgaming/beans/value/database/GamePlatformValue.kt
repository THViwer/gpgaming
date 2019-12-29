package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.Status

sealed class GamePlatformValue {

    data class GamePlatformCo(

            // 平台
            val platform: Platform,

            // 平台名称
            val name: String,

            // 图标
            val icon: String,

            // 平台维护图标
            val disableIcon: String?,

            // 原始图标
            val originIcon: String,

            // 原始鼠标移上去图标
            val originIconOver: String,

            // 是否有试玩
            val demo: Boolean = false,

            // 状态
            val status: Status,

            // 启动列表
            val launchs: String
    )


    data class GamePlatformUo(

            val id: Int,

            // 平台
            val platform: Platform,

            // 平台名称
            val name: String,

            // 图标
            val icon: String,

            // 平台维护图标
            val disableIcon: String?,

            // 原始图标
            val originIcon: String,

            // 原始鼠标移上去图标
            val originIconOver: String,

            // 是否有试玩
            val demo: Boolean = false,

            // 状态
            val status: Status,

            // 启动列表
            val launchs: String
    )

}