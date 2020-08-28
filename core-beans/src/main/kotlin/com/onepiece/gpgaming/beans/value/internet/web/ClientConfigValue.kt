package com.onepiece.gpgaming.beans.value.internet.web

import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.gpgaming.beans.enums.ShowPosition
import java.math.BigDecimal

sealed class ClientConfigValue {

    data class ClientConfigUo(
            val clientId: Int,

            val title: String,

            val keywords: String,

            val description: String,

            // 在线聊天Id
            val liveChatId: String,

            // 是否打开新的窗口
            val liveChatTab: Boolean,

            // google统计Id
            val googleStatisticsId: String,

            //面子书广告
            val facebookTr: String,

            //facebook 显示位置
            val facebookShowPosition: ShowPosition,

            // asg content
            val asgContent: String

    )

    data class ClientConfigVo(
            val title: String,

            val keywords: String,

            val description: String,

            // 在线聊天Id
            val liveChatId: String,

            // 是否打开新的窗口
            val liveChatTab: Boolean,

            // google统计Id
            val googleStatisticsId: String,

            //面子书广告
            val facebookTr: String,

            //facebook 显示位置
            val facebookShowPosition: ShowPosition,

            // asg content
            val asgContent: String
    )

    data class IntroduceUo(

            @JsonIgnore
            val clientId: Int,

            /** 会员推广佣金 */
            // 是否开启会员介绍
            val enableIntroduce: Boolean,
            // 会员介绍优惠活动Id(category=Introduce)
            val introducePromotionId: Int,
            // 注册佣金
            val registerCommission: BigDecimal,
            // 周期内需要充值金额
            val depositPeriod: BigDecimal,
            // 充值周期
            val commissionPeriod: Int,
            // 充值佣金
            val depositCommission: BigDecimal,
            // 分享模板
            val shareTemplate: String
    )

}