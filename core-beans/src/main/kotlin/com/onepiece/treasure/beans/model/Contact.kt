package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.ContactType
import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

/**
 * 联系我们
 */
data class Contact (

        val id: Int,

        val clientId: Int,

        val type: ContactType,

        val number: String,

        // 二维码图片
        val qrCode: String?,

        val status: Status,

        val createdTime: LocalDateTime
)