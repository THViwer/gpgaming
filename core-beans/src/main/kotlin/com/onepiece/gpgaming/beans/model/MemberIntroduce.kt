package com.onepiece.gpgaming.beans.model

import java.time.LocalDateTime

data class MemberIntroduce(

        // 用户Id
        val id: Int,

        // 会员Id
        val memberId: Int,

        // 介绍会员Id
        val introduceMemberId: Int,

        // 创建时间
        val createTime: LocalDateTime

)