package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.ClientConfig
import com.onepiece.gpgaming.beans.value.internet.web.ClientConfigValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface ClientConfigDao: BasicDao<ClientConfig> {

    fun create(configUo: ClientConfigValue.ClientConfigUo): Boolean

    fun update(configUo: ClientConfigValue.ClientConfigUo): Boolean

    fun update(id: Int, enableRegisterMessage: Boolean, registerMessageTemplate: String, regainMessageTemplate: String): Boolean

    fun update(uo: ClientConfigValue.IntroduceUo): Boolean
}