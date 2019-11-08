package com.onepiece.treasure.core

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.core.service.PlatformBindService
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrderIdBuilder(
        private val platformBindService: PlatformBindService
) {

    fun generatorDepositOrderId(): String {
        return UUID.randomUUID().toString()
    }

    fun generatorWithdrawOrderId(): String {
        return UUID.randomUUID().toString()
    }

    fun generatorTransferOrderId(clientId: Int, platform: Platform): String {

        return when (platform) {
            Platform.Sbo -> {
                val clientToken = platformBindService.find(clientId = clientId, platform = platform).clientToken as DefaultClientToken
                "${clientToken.appId}-${UUID.randomUUID().toString().replace("-", "").substring(0, 6)}"

            }
            else -> UUID.randomUUID().toString()
        }

    }

    fun generatorArtificialOrderId(): String {
        return UUID.randomUUID().toString()
    }


}