package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.PayBind
import com.onepiece.gpgaming.beans.value.database.PayBindValue

interface PayBindService {

    fun all(clientId: Int): List<PayBind>

    fun get(clientId: Int, id: Int): PayBind

    fun list(clientId: Int): List<PayBind>

    fun create(co: PayBindValue.PayBindCo)

    fun update(uo: PayBindValue.PayBindUo)


}