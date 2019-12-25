package com.onepiece.gpgaming.su.controller.value

import com.onepiece.gpgaming.beans.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

sealed class ClientSuValue {

    data class ClientVo(

            @ApiModelProperty("厅主Id")
            val id: Int,

            @ApiModelProperty("用户名")
            val username: String,

            @ApiModelProperty("logo")
            val logo: String,

            @ApiModelProperty("昵称")
            val name: String,

            @ApiModelProperty("平台开通数量")
            val openNumber: Int,

            @ApiModelProperty("状态")
            val status: Status,

            @ApiModelProperty("创建时间")
            val createdTime: LocalDateTime

    )

    data class ClientCoReq(

            @ApiModelProperty("用户名")
            val username: String,

            @ApiModelProperty("logo")
            val logo: String,

            @ApiModelProperty("昵称")
            val name: String,

            @ApiModelProperty("密码")
            val password: String

    )

    data class ClientUoReq(

            @ApiModelProperty("用户Id")
            val id: Int,

            @ApiModelProperty("昵称")
            val name: String?,

            @ApiModelProperty("logo")
            val logo: String?,

            @ApiModelProperty("用户密码")
            val password: String?,

            @ApiModelProperty("状态")
            val status: Status?

    )


}