package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.AdvertType
import com.onepiece.treasure.beans.enums.Status

data class AdvertCo(

        // 厅主Id
        val clientId: Int,

        // 图标
        val icon: String,

        // 鼠标移动上去图标
        val touchIcon: String?,

        // 位置
        val position: AdvertType,

        // 排序
        val order: Int,

        // 连接地址
        val link: String
)

data class AdvertUo(

        val id: Int,

        // 图标
        val icon: String?,

        // 鼠标移动上去图标
        val touchIcon: String?,

        // 位置
        val position: AdvertType?,

        // 排序
        val order: Int?,

        // 连接地址
        val link: String?,

        // 状态
        val status: Status?
)