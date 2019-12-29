package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status

data class GamePlatform(
        val id: Int,

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

) {

    // 启动方式
    val launchList: List<LaunchMethod>
        get() {
            return launchs.split(",").map { LaunchMethod.valueOf(it) }
        }
}