package com.onepiece.gpgaming.beans.model

import java.time.LocalDateTime

/**
 * 会员关系
 */
data class MemberRelation (

        // id
        val id: Int,

        // bossId
        val bossId: Int,

        // 会员Id
        val memberId: Int,

        // r1 代理
        val r1: Int,

        // r2 代理
        val r2: Int?,

        // 创建时间
        val createdTime: LocalDateTime
)
