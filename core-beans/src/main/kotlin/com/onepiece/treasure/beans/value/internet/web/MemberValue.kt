package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.time.LocalDateTime

object MemberValueFactory {

    fun generatorMemberPage(): MemberPage {

        val now = LocalDateTime.now()
        val m1 = MemberVo(id = 1, username = "zhangsan", levelId = 1, level = "默认", name = "张三", balance = BigDecimal.valueOf(100),
                status = Status.Normal, createdTime = now, loginTime = now, loginIp = "198.234.1.23")
        val m2 = m1.copy(id = 2, username = "lisi", levelId = 2, level = "vip", name = "李四", balance = BigDecimal.ZERO)

        val data = listOf(m1, m2)
        return MemberPage(data = data, total = 100)
    }


}
//
//data class MemberQuery (
//
//        val id: Int,
//
//        @ApiModelProperty("用户名")
//        val username: String?,
//
//        @ApiModelProperty("层级Id")
//        val levelId: Int?,
//
//        @ApiModelProperty("状态")
//        val status: Status?,
//
//        @ApiModelProperty("当前数据")
//        val current: Int = 0,
//
//        @ApiModelProperty("每页条数")
//        val size: Int = 10
//)

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

        @ApiModelProperty("姓名")
        val name: String,

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