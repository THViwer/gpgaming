package com.onepiece.gpgaming.beans.model

import java.time.LocalDateTime

data class MemberIntroduce(

        // 用户Id
        val id: Int,

        // 会员Id
        val memberId: Int,

        // 介绍会员Id
        val introduceMemberId: Int,

        // 是否已完成注册活动
        val registerActivity: Boolean,

        // 是否已完成充值活动
        val depositActivity: Boolean,

        // 介绍人员操作优惠活动Id(如果没有，则需要会员选择优惠并进行自动转账)
        val introducePromotionId: Int,

        // 会员姓名
        val name: String,

        // 会员注册ip
        val registerIp: String,

        // 创建时间
        val createTime: LocalDateTime

)