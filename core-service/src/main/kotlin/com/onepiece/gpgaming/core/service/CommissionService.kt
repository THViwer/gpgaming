package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.enums.CommissionType
import com.onepiece.gpgaming.beans.model.Commission
import com.onepiece.gpgaming.beans.value.database.CommissionValue

interface CommissionService {

    fun all(): List<Commission>

    fun list(bossId: Int, type: CommissionType): List<Commission>

    fun create(co: CommissionValue.CommissionCo)

    fun update(uo: CommissionValue.CommissionUo)

}