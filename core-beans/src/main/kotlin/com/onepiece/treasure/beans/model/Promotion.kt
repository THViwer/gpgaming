package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.PromotionCategory
import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

/**
 * 优惠活动
 */
data class Promotion (

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 优惠类型
        val category: PromotionCategory,

        // 结束时间, 如果为null 则无限时间
        val stopTime: LocalDateTime?,

        // 是否置顶
        val top: Boolean,

        // 图标
        val icon: String,

        // 标题
        val title: String,

        // 简介
        val synopsis: String?,

        // 内容
        val content: String,

        // 活动状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime,

        // 更新时间
        val updatedTime: LocalDateTime

)
