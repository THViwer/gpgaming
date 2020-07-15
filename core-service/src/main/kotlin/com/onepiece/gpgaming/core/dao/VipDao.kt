package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Vip
import com.onepiece.gpgaming.beans.value.database.VipValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface VipDao: BasicDao<Vip> {

    fun create(co: VipValue.VipCo): Boolean

    fun update(uo: VipValue.VipUo): Boolean

}