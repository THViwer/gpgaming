package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.Status
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

        @ApiModelProperty("层级Id")
        val levelId: Int,

        @ApiModelProperty("层级名称")
        val level: String,

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

data class MemberUoReq(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("层级Id")
        val levelId: Int?,

        @ApiModelProperty("密码")
        val password: String?,

        @ApiModelProperty("状态")
        val status: Status
)

data class MemberCoReq(

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("密码")
        val password: String,

        @ApiModelProperty("安全密码")
        val safetyPassword: String,

        @ApiModelProperty("层级")
        val levelId: Int

)