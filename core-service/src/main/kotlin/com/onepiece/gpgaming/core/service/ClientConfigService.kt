package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.ClientConfig
import com.onepiece.gpgaming.beans.value.internet.web.ClientConfigValue


interface ClientConfigService {

    fun get(clientId: Int): ClientConfig

    fun update(configUo: ClientConfigValue.ClientConfigUo)

    fun update(id: Int, enableRegisterMessage: Boolean, registerMessageTemplate: String)

}