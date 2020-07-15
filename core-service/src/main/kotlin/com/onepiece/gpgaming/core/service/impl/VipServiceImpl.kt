package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Vip
import com.onepiece.gpgaming.beans.value.database.VipValue
import com.onepiece.gpgaming.core.dao.VipDao
import com.onepiece.gpgaming.core.service.VipService
import org.springframework.stereotype.Service

@Service
class VipServiceImpl(
        private val vipDao: VipDao
) : VipService {

    override fun get(vipId: Int): Vip {
        return vipDao.get(vipId)
    }

    override fun list(clientId: Int): List<Vip> {
        return vipDao.all(clientId = clientId)
    }

    override fun create(co: VipValue.VipCo) {
        val flag = vipDao.create(co = co)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(uo: VipValue.VipUo) {
        val flag = vipDao.update(uo = uo)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}