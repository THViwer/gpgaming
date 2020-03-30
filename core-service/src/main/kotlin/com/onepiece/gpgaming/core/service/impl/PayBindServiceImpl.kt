package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.PayBind
import com.onepiece.gpgaming.beans.value.database.PayBindValue
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.PayBindDao
import com.onepiece.gpgaming.core.service.PayBindService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.stereotype.Service

@Service
class PayBindServiceImpl(
        private val  redisService: RedisService,
        private val payBindDao: PayBindDao
) : PayBindService {

    override fun all(clientId: Int): List<PayBind> {
        val redisKey = OnePieceRedisKeyConstant.getPayBinds(clientId)
        return redisService.getList(key = redisKey, clz = PayBind::class.java) {
            payBindDao.all(clientId = clientId)
        }
    }

    override fun get(clientId: Int, id: Int): PayBind {
        return this.list(clientId = clientId).first { it.id == id }
    }

    override fun list(clientId: Int): List<PayBind> {
        return  this.all(clientId).filter { it.status == Status.Normal }
    }

    override fun create(co: PayBindValue.PayBindCo) {

        val flag = payBindDao.create(co)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.getPayBinds(co.clientId))
    }

    override fun update(uo: PayBindValue.PayBindUo) {

        val flag = payBindDao.update(uo)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.getPayBinds(uo.clientId))
    }
}