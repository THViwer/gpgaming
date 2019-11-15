package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class LevelVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("名称")
        val name: String,

        @ApiModelProperty("总人数")
        val total: Int,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime
)

data class LevelUoReq(
        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("名称")
        val name: String?,

        @ApiModelProperty("状态")
        val status: Status?
)

data class LevelMemberQuery(

        @ApiModelProperty("用户名")
        val username: String?,

        @ApiModelProperty("最小金额")
        val minBalance: BigDecimal?,

        @ApiModelProperty("最小总充值金额")
        val minTotalDepositBalance: BigDecimal?,

        @ApiModelProperty("最小总取款金额")
        val minTotalWithdrawBalance: BigDecimal?,

        @ApiModelProperty("最小充值次数")
        val minTotalDepositFrequency: Int?,

        @ApiModelProperty("最小取款次数")
        val minTotalWithdrawFrequency: Int?

)

data class LevelMemberVo(

        @ApiModelProperty("会员Id")
        val memberId: Int,

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("余额")
        val balance: BigDecimal,

        @ApiModelProperty("冻结金额")
        val freezeBalance: BigDecimal,

        @ApiModelProperty("总充值金额")
        val totalDepositBalance: BigDecimal,

        @ApiModelProperty("总取款金额")
        val totalWithdrawBalance: BigDecimal,

        @ApiModelProperty("总优惠金额")
        val totalGiftBalance: BigDecimal,

        @ApiModelProperty("总存款次数")
        val totalDepositFrequency: Int,

        @ApiModelProperty("总提款次数")
        val totalWithdrawFrequency: Int,

        @ApiModelProperty("层级Id")
        val levelId: Int,

        @ApiModelProperty("层级名称")
        val levelName: String,

        @ApiModelProperty("登陆时间")
        val loginTime: LocalDateTime?

)

data class LevelCoReq(

        @ApiModelProperty("名称")
        val name: String

)

data class LevelMoveDo(

        @ApiModelProperty("移动层级人的Id")
        val memberIds: List<Int>,

        @ApiModelProperty("移动到的层级")
        val levelId: Int

)

//data class LevelMoveVo(
//
//        @ApiModelProperty("执行序列")
//        val sequence: String
//)

data class LevelMoveCheckVo(

        @ApiModelProperty("是否执行完成")
        val done: Boolean
)