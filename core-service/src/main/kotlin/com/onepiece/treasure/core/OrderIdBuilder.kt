package com.onepiece.treasure.core

import org.springframework.stereotype.Component
import java.util.*

@Component
class OrderIdBuilder {

    fun generatorDepositOrderId(): String {
        return UUID.randomUUID().toString()
    }

    fun generatorWithdrawOrderId(): String {
        return UUID.randomUUID().toString()
    }

    fun generatorTransferOrderId(): String {
        return UUID.randomUUID().toString()
    }


}