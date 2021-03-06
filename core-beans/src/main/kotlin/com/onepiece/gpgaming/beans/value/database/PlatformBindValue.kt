package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal

data class PlatformBindCo(

        // 厅主Id
        val clientId: Int,

        // 保证金
        val earnestBalance: BigDecimal,

        // token信息
        val tokenJson: String,

        // 用户名
        val username: String,

        // 密码
        val password: String,

        // 平台
        val platform: Platform,

        // 平台名称
        val name: String,

        // 图标
        val icon: String,

        // unclejay大的横向图标
        val unclejayMobleIcon : String?,

        // 平台维护图标
        val disableIcon: String?,

        // 原始图标
        val originIcon: String,

        // 原始鼠标移上去图标
        val originIconOver: String,

        // 手机图标
        val mobileIcon: String,

        // 手机平台维护图标
        val mobileDisableIcon: String?,

        // 平台详细图标
        val platformDetailIcon: String?,

        // 平台详情鼠标移动图片
        val platformDetailIconOver: String?

)

data class PlatformBindUo(

        // id
        val id: Int,

        // 用户名
        val username: String?= null,

        // 是否热门
        val hot: Boolean?= null,

        // 是否新游戏
        val new: Boolean?= null,

        // 密码
        val password: String?= null,

        // token信息
        val tokenJson: String?= null,

        // 保证金
        val earnestBalance: BigDecimal?= null,

        // 状态
        val status: Status?= null,

        // 平台名称
        val name: String?= null,

        // 图标
        val icon: String?= null,

        val unclejayMobleIcon : String?= null,

        // 平台维护图标
        val disableIcon: String?= null,

        // 原始图标
        val originIcon: String?= null,

        // 原始鼠标移上去图标
        val originIconOver: String?= null,

        // 手机图标
        val mobileIcon: String?= null,

        // 手机平台维护图标
        val mobileDisableIcon: String? = null,

        // 平台详细图标
        val platformDetailIcon: String? = null,

        // 平台详情鼠标移动图片
        val platformDetailIconOver: String? = null

)