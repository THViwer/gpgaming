package com.onepiece.gpgaming.mr.selenium

import java.math.BigDecimal

data class SeleniumData (

        // 用户名
        val username: String,

        // 下注笔数
        val betFrequency: Int,

        // 总下注
        val totalBet: BigDecimal,

        // 总有效投注
        val totalValidBet: BigDecimal,

        // 会员获利(当前时间段)
        val memberProfit: BigDecimal,

        // 代理获利(当前时间段)
        val agentProfit: BigDecimal,

        // 公司获利(当前时间段)
        val companyProfit: BigDecimal

)