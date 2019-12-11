package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.ContactType
import com.onepiece.treasure.beans.enums.Status
import io.swagger.annotations.ApiModelProperty

sealed class ContactValue {

    data class Create(
            @ApiModelProperty("号码(微信号或whatsapp)")
            val number: String,

            @ApiModelProperty("二维码图片")
            val qrCode: String?,

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