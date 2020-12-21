package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.ShowPosition
import java.math.BigDecimal
import java.time.LocalDateTime

data class ClientConfig(

        // id
        val id: Int,
        // 厅主
        val clientId: Int,

        /** seo相关设置 */
        // 标题
        val title: String,
        // 关键字
        val keywords: String,
        // 描述
        val description: String,
        // google统计Id
        val googleStatisticsId: String,

        val gtag: String,
        //面子书广告
        val facebookTr: String,
        // facebook显示位置
        val facebookShowPosition: ShowPosition,
        // asg 广告内容
        val asgContent: String,

        val telegram: String,

        val oneSingal: String,

        /** live chat  */
        // 在线聊天Id
        val liveChatId: String,
        // 是否打开新的页面
        val liveChatTab: Boolean,

        /** 短信 */
        // 是否开启注册消息
        val enableRegisterMessage: Boolean,
        // 短信注册模板
        val registerMessageTemplate: String,
        // 找加密码模板
        val regainMessageTemplate: String,


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
        // 分享消息模板
        val shareTemplate: String,

        // 最低出款要求 (出款必须有充值 且充值过的金额 大于 该值)
        val minWithdrawRequire: BigDecimal,

        // vip 背景图介绍
        val vipIntroductionImage: String?,


        // 创建时间
        val createdTime: LocalDateTime
)