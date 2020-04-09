package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.WalletEvent
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

sealed class WalletNoteValue {

    data class WalletNoteVo(

            @ApiModelProperty("事件Id")
            val eventId: String?,

            @ApiModelProperty("事件")
            val event: WalletEvent,

            @ApiModelProperty("操作金额")
            val money: BigDecimal,

            @ApiModelProperty("原始金额")
            val originMoney: BigDecimal,

            @ApiModelProperty("操作后金额")
            val afterMoney: BigDecimal,

            @ApiModelProperty("优惠金额")
            val promotionMoney: BigDecimal?,

            @ApiModelProperty("备注")
            val remarks: String,

            @ApiModelProperty("创建时间")
            val createdTime: LocalDateTime

    )

}