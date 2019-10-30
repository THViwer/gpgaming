package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.TransferOrder
import com.onepiece.treasure.beans.value.database.TransferOrderCo
import com.onepiece.treasure.beans.value.database.TransferOrderUo
import com.onepiece.treasure.core.dao.basic.BasicDao

interface TransferOrderDao: BasicDao<TransferOrder> {

    fun create(transferOrderCo: TransferOrderCo): Boolean

    fun update(transferOrderUo: TransferOrderUo): Boolean

}