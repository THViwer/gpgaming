package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.ContactType
import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDateTime

/**
 * 联系我们
 */
data class Contact (

        // id
        val id: Int,

        // 业主Id
        val clientId: Int,

        // 类型
        val type: ContactType,

        // 号码
        val number: String,

        // 二维码图片
        val qrCode: String?,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime
)