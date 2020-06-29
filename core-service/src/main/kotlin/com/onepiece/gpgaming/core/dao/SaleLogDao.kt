package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.SaleLog
import com.onepiece.gpgaming.beans.value.database.SaleLogValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao


interface SaleLogDao: BasicDao<SaleLog> {

    fun create(co: SaleLogValue.SaleLogCo): Boolean

    fun list(query: SaleLogValue.SaleLogQuery): List<SaleLog>

}