package com.onepiece.gpgaming.player.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestBody

@Api(tags = ["payment"], description = " ")
interface PayBackApi  {

    @ApiOperation(tags = ["payment"], value = "m3pay")
    fun m3pay()

    @ApiOperation(tags = ["payment"], value = "surepay")
    fun surepay()

    @ApiOperation(tags = ["payment"], value = "gppay")
    fun gppay(@RequestBody req: PayBackApiController.MerchantNotifyReq)


    @ApiOperation(tags = ["payment"], value = "instantpay")
    fun instantpay(@RequestBody req: InstantPayResponse)


    data class InstantPayResponse(
            val amount: Int,

            val charge: Int,

            /**
             * 0 Represent Pending
             * 2 Represent Success
             * 3 Represent Failed
             */
            val transactionStatus: Int,

            val createdDateTime: String,

            val modificationDateTime: String,

            val transactionId: String,

            val platformTransactionId: String,

            val timestamp: Int,

            val playerId: String,

            val sign: String
    )
}