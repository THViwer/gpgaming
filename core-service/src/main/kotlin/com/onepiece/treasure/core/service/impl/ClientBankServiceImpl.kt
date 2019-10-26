package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.ClientBank
import com.onepiece.treasure.beans.value.database.ClientBankCo
import com.onepiece.treasure.beans.value.database.ClientBankUo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.ClientBankDao
import com.onepiece.treasure.core.service.ClientBankService
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service

@Service
class ClientBankServiceImpl(
        private val clientBankDao: ClientBankDao,
        private val redisService: RedisService
): ClientBankService {

    override fun get(id: Int): ClientBank {
        //TODO 使用缓存
        return clientBankDao.get(id)
    }

    override fun findClientBank(clientId: Int): List<ClientBank> {
        val redisKey = OnePieceRedisKeyConstant.clientBanks(clientId)
        return redisService.getList(redisKey, ClientBank::class.java) {
            clientBankDao.findClientBank(clientId)
        }
    }

    override fun create(clientBankCo: ClientBankCo) {
        val state = clientBankDao.create(clientBankCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.clientBanks(clientBankCo.clientId))
    }

    override fun update(clientBankUo: ClientBankUo) {

        val hasClientBank = clientBankDao.get(clientBankUo.id)

        val state = clientBankDao.update(clientBankUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.clientBanks(hasClientBank.clientId))
    }
}