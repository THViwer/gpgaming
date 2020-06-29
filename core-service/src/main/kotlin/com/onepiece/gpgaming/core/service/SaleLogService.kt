package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.SaleLog
import com.onepiece.gpgaming.beans.value.database.SaleLogValue
import org.springframework.scheduling.annotation.Async

interface SaleLogService {

    @Async
    fun create(co: SaleLogValue.SaleLogCo)

    fun list(query: SaleLogValue.SaleLogQuery): List<SaleLog>

}