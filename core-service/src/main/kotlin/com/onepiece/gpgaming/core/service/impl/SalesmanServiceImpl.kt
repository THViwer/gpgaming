package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.Salesman
import com.onepiece.gpgaming.core.dao.SalesmanDao
import com.onepiece.gpgaming.core.service.SalesmanService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.stereotype.Service

@Service
class SalesmanServiceImpl(
        private val redisService: RedisService,
        private val salesmanDao: SalesmanDao
) : SalesmanService {

    override fun login(username: String, password: String): Salesman {
        TODO("Not yet implemented")
    }

    override fun select(bossId: Int, clientId: Int, saleId: Int?): Salesman? {

        fun selectNext(): Salesman? {
            val redisKey = "salesman:id:$clientId"
            val cacheSaleId = redisService.get(key = redisKey, clz = Int::class.java) ?: -1
            return salesmanDao.all(clientId = clientId).filter { bossId == it.bossId }
                    .let { list ->
                        list.firstOrNull { it.id > cacheSaleId } ?: list.firstOrNull()
                    }?.also {
                        redisService.put(key = redisKey, value = it.id)
                    }
        }

        return saleId?.let {
            salesmanDao.get(saleId)
        } ?: selectNext()
    }
}