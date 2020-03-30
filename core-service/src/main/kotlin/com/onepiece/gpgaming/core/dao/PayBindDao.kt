package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.PayBind
import com.onepiece.gpgaming.beans.value.database.PayBindValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface PayBindDao: BasicDao<PayBind> {

    fun create(co: PayBindValue.PayBindCo):Boolean

    fun update(uo: PayBindValue.PayBindUo): Boolean

}