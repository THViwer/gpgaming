package com.onepiece.gpgaming.player.controller.value

import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Role
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.time.LocalDate

sealed class UserValue {

    data class UserInfoUo(

            val email: String?,

            val birthday: LocalDate?
    )

    data class MyIntroduceDetail(

            // 介绍总数
            val introduceCount: Int,

            // 介绍佣金
            val commission: BigDecimal
    )
}

data class LoginResp(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("层级Id")
        val levelId: Int,

        @ApiModelProperty("vip id")
        val vipId: Int,

        @ApiModelProperty("vip 名称")
        val vipName: String,

        @ApiModelProperty("vip logo")
        val vipLogo: String,

        @ApiModelProperty("真实姓名")
        val name: String,

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("角色")
        val role: Role,

        @ApiModelProperty("token")
        val token: String,

        @ApiModelProperty("是否开启自动转账")
        val autoTransfer: Boolean,

        @ApiModelProperty("域名")
        val domain: String,

        @ApiModelProperty("当前用户国家")
        val country: Country,

        @ApiModelProperty("是否需要国家跳转")
        val successful: Boolean,

        @ApiModelProperty("推荐注册金活动")
        val registerActivity: Boolean,

        @ApiModelProperty("推荐注册金活动")
        val registerActivityVo: RegisterActivityVo?,

        @ApiModelProperty("推荐充值金活动")
        val depositActivity: Boolean

) {

    data class RegisterActivityVo(

            @ApiModelProperty("金额")
            val amount: BigDecimal,

            @ApiModelProperty("标题")
            val title: String,

            @ApiModelProperty("平台")
            val platforms: List<Platform>,

            @ApiModelProperty("优惠活动Id")
            val promotionId: Int

    )

}

data class CheckUsernameResp(
        @ApiModelProperty("是否存在")
        val exist: Boolean
)

data class LoginReq(

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("密码")
        val password: String
)

data class LoginByAdminReq(

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("clientId")
        val clientId: Int,

        @ApiModelProperty("现在时间")
        val time: Long,

        @ApiModelProperty("hash")
        val hash: String
)

data class LoginByAdminResponse(

        val loginPath: String
)

data class RegisterReq(

        @ApiModelProperty("国家")
        val country: Country,

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("密码")
        val password: String,

        @ApiModelProperty("用户真实姓名")
        val name: String = "张三",

        @ApiModelProperty("手机号")
        val phone: String,

        @ApiModelProperty("安全密码")
        val safetyPassword: String = "1234",

        @ApiModelProperty("推广码")
        val promoteCode: String?,

        @ApiModelProperty("电销人员Id")
        val saleCode: String?,

        @ApiModelProperty("营销Id")
        val marketId: Int?,

        @ApiModelProperty("介绍Id")
        val introduceId: Int?,

        @ApiModelProperty("链路code")
        val chainCode: String?,

        @ApiModelProperty("邮箱")
        val email: String?,

        @ApiModelProperty("出生日期")
        val birthday: LocalDate?
)

data class ChangePwdReq(

        @ApiModelProperty("旧密码")
        val oldPassword: String,

        @ApiModelProperty("新密码")
        val password: String

)

data class PlatformMemberVo(
        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("平台")
        val platform: Platform,

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("密码")
        val password: String,

        @ApiModelProperty("排序")
        val sort: Int
) {

    val pname: String
        @ApiModelProperty("平台名称")
        get() {
            return platform.pname
        }
}

data class PlatformMemberUo(

        @ApiModelProperty("平台")
        val platform: Platform,

        @ApiModelProperty("密码")
        val password: String
)

