package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.ClientBank
import com.onepiece.gpgaming.beans.value.database.ClientBankCo
import com.onepiece.gpgaming.beans.value.database.ClientBankUo
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.ClientBankDao
import com.onepiece.gpgaming.core.service.ClientBankService
import com.onepiece.gpgaming.utils.RedisService
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