package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Deposit
import com.onepiece.gpgaming.beans.model.Wallet
import com.onepiece.gpgaming.beans.model.Withdraw
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.time.LocalDateTime


data class MemberPage(

        val data: List<MemberVo>,

        val total: Int
)

data class MemberVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("姓名")
        val name: String,

        @ApiModelProperty("层级Id")
        val levelId: Int,

        @ApiModelProperty("层级名称")
        val level: String,

        @ApiModelProperty("手机号")
        val phone: String,

//        @ApiModelProperty("姓名")
//        val name: String,

        @ApiModelProperty("余额(中心钱包)")
        val balance: BigDecimal,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime,

        @ApiModelProperty("登陆Ip")
        val loginIp: String?,

        @ApiModelProperty("登陆时间")
        val loginTime: LocalDateTime?

)

data class MemberWalletInfo(

        // id
        val memberId: Int,

        // 钱包详情
        val wallet: Wallet,

        // 平台余额列表
        val balances: List<BalanceVo>,

        // 最后5个充值信息
        val lastFiveDeposit: List<Deposit>,

        // 最后5个取款信息
        val lastFiveWithdraw: List<Withdraw>

) {

        data class BalanceVo(

                // 平台
                val platform: Platform,

                // 当前余额
                val balance: BigDecimal,

                // 总打码量
                val totalBet: BigDecimal,

                // 总盈利
                val totalWin: BigDecimal,

                // 总充值金额
                val totalAmount: BigDecimal,

                // 总出款金额
                val totalTransferOutAmount: BigDecimal,

                // 总优惠金额
                val totalPromotionAmount: BigDecimal
        )


}

data class MemberUoReq(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("姓名")
        val name: String?,

        @ApiModelProperty("层级Id")
        val levelId: Int?,

        @ApiModelProperty("密码")
        val password: String?,

        @ApiModelProperty("状态")
        val status: Status?
)

data class MemberCoReq(

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("姓名")
        val name: String,

        @ApiModelProperty("phone")
        val phone: String,

        @ApiModelProperty("密码")
        val password: String,

        @ApiModelProperty("安全密码")
        val safetyPassword: String,

        @ApiModelProperty("层级")
        val levelId: Int

)