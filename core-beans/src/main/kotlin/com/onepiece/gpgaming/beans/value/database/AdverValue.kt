package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.Status

data class BannerCo(

        // 厅主Id
        val clientId: Int,

//        // 图标
//        val icon: String,
//
//        // 鼠标移动上去图标
//        val touchIcon: String?,

        // 位置
        val type: BannerType,

        val platformCategory: PlatformCategory?,

        // 排序
        val order: Int,

        // 连接地址
        val link: String?
)

data class BannerUo(

        val id: Int,

//        // 图标
//        val icon: String?,
//
//        // 鼠标移动上去图标
//        val touchIcon: String?,

        // 位置
        val type: BannerType?,

        val platformCategory: PlatformCategory?,

        // 排序
        val order: Int?,

        // 连接地址
        val link: String?,

        // 状态
        val status: Status?
)