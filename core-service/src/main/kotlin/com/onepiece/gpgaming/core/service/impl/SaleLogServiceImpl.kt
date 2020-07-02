package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.SaleLog
import com.onepiece.gpgaming.beans.value.database.MemberInfoValue
import com.onepiece.gpgaming.beans.value.database.SaleLogValue
import com.onepiece.gpgaming.core.dao.SaleLogDao
import com.onepiece.gpgaming.core.service.MemberInfoService
import com.onepiece.gpgaming.core.service.SaleLogService
import org.springframework.stereotype.Service


@Service
class SaleLogServiceImpl(
        private val saleLogDao: SaleLogDao,
        private val memberInfoService: MemberInfoService
) : SaleLogService {

    override fun create(co: SaleLogValue.SaleLogCo) {
        val flag = saleLogDao.create(co = co)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        val infoUo = MemberInfoValue.MemberInfoUo.ofSale(memberId = co.memberId, nextCallTime = co.nextCallTime)
        memberInfoService.asyncUpdate(infoUo)
    }

    override fun list(query: SaleLogValue.SaleLogQuery): List<SaleLog> {
        return saleLogDao.list(query = query)
    }
}