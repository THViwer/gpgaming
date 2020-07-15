package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.Vip
import com.onepiece.gpgaming.beans.value.database.VipValue

interface VipService {

    fun list(clientId: Int): List<Vip>

    fun create(co: VipValue.VipCo)

    fun update(uo: VipValue.VipUo)

}