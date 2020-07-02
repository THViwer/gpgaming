package com.onepiece.gpgaming.beans.model

import java.time.LocalDateTime

data class SaleLog(

        // id
        val id: Int,

        // bossId
        val bossId: Int,

        // clientId
        val clientId: Int,

        // 电销Id
        val saleId: Int,

        // 会员Id
        val memberId: Int,

        // 备注
        val remark: String,

        // 下一次电话时间
        val nextCallTime: LocalDateTime?,

        // 创建时间
        val createdTime: LocalDateTime

)