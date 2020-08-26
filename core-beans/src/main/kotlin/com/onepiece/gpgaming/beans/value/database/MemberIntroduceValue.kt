package com.onepiece.gpgaming.beans.value.database

import java.math.BigDecimal

sealed class MemberIntroduceValue {

    data class MemberIntroduceCo(

            // 会员Id
            val memberId: Int,

            // 介绍会员Id
            val introduceId: Int,

            // 介绍人员操作优惠活动Id(如果没有，则需要会员选择优惠并进行自动转账)
//            val introducePromotionId: Int?,

            // 会员姓名
            val name: String,

            // 会员注册ip
            val registerIp: String
    )

    data class MemberIntroduceUo(
            val id: Int,

            val registerActivity: Boolean?,

            val depositActivity: Boolean?,

            val introduceCommission: BigDecimal?

            // 介绍人员操作优惠活动Id(如果没有，则需要会员选择优惠并进行自动转账)
//            val introducePromotionId: Int
    )

    data class MemberIntroduceQuery(

            // 介绍会员Id
            val introduceId: Int

    )

}