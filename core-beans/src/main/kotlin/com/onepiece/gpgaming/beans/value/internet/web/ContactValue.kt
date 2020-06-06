package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.ContactType
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import io.swagger.annotations.ApiModelProperty

sealed class ContactValue {

    data class Create(
            @ApiModelProperty("号码(微信号或whatsapp)")
            val number: String,

            @ApiModelProperty("二维码图片")
            val qrCode: String?,

            @ApiModelProperty("角色 Member / Agent")
            val role: Role = Role.Member,

            @ApiModelProperty("类型")
            val type: ContactType
    )

    data class Update(
            @ApiModelProperty("id")
            val id: Int,

            @ApiModelProperty("号码(微信号或whatsapp)")
            val number: String,


            @ApiModelProperty("二维码图片")
            val qrCode: String?,

            @ApiModelProperty("类型")
            val status: Status
    )

}