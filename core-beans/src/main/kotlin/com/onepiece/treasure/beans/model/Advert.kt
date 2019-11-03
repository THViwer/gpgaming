package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.AdvertType
import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

/**
 * 首页广告
 */
data class Advert(

        // id
        val id: Int,

        // 排序
        val order: Int,

        // 厅主Id
        val clientId: Int,

        // 图标
        val icon: String,

        // 鼠标移动上去图标
        val touchIcon: String?,

        // 位置
        val position: AdvertType,

        // 连接地址
        val link: String,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime,

        // 更新时间
        val updatedTime: LocalDateTime

)