package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Salesman
import com.onepiece.gpgaming.beans.value.database.SalesmanValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface SalesmanDao: BasicDao<Salesman> {

    fun create(co: SalesmanValue.SalesmanCo): Boolean

    fun update(uo: SalesmanValue.SalesmanUo): Boolean

    fun list(query: SalesmanValue.SalesmanQuery): List<Salesman>


}