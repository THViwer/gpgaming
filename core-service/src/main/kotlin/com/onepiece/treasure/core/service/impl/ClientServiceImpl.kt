package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Client
import com.onepiece.treasure.beans.value.database.ClientCo
import com.onepiece.treasure.beans.value.database.ClientUo
import com.onepiece.treasure.beans.value.database.LevelCo
import com.onepiece.treasure.beans.value.database.LoginValue
import com.onepiece.treasure.core.dao.ClientDao
import com.onepiece.treasure.core.service.ClientService
import com.onepiece.treasure.core.service.LevelService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ClientServiceImpl(
        private val clientDao: ClientDao,
        private val levelService: LevelService
) : ClientService {

    override fun login(loginValue: LoginValue): Client {
        val client = clientDao.findByUsername(loginValue.username)
        checkNotNull(client) { OnePieceExceptionCode.LOGIN_FAIL }
        check(loginValue.password == client.password) { OnePieceExceptionCode.LOGIN_FAIL }

        // update client
        val clientUo = ClientUo(id = client.id, ip = loginValue.ip, loginTime = LocalDateTime.now())
        this.update(clientUo)

        return client.copy(password = "")
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun create(clientCo: ClientCo) {

        // check username exist
        val hasClient = clientDao.findByUsername(clientCo.username)
        check(hasClient == null) { OnePieceExceptionCode.USERNAME_EXISTENCE }

        // insert client
        val id = clientDao.create(clientCo)
        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // create default level
        val levelCo = LevelCo(clientId = id, name = "default")
        levelService.create(levelCo)

    }

    override fun update(clientUo: ClientUo) {
        val state = clientDao.update(clientUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}