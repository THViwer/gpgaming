package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Commission
import com.onepiece.gpgaming.beans.value.database.CommissionValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface CommissionDao : BasicDao<Commission> {

    fun list(bossId: Int): List<Commission>

    fun create(co: CommissionValue.CommissionCo): Boolean

    fun update(uo: CommissionValue.CommissionUo): Boolean

}