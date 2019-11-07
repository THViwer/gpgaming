package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Client
import com.onepiece.treasure.beans.value.database.*
import com.onepiece.treasure.core.dao.ClientDao
import com.onepiece.treasure.core.service.BalanceService
import com.onepiece.treasure.core.service.ClientService
import com.onepiece.treasure.core.service.LevelService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ClientServiceImpl(
        private val clientDao: ClientDao,
        private val levelService: LevelService,
        private val balanceService: BalanceService
) : ClientService {

    override fun all(): List<Client> {
        return clientDao.all()
    }

    override fun login(loginValue: LoginValue): Client {
        val client = clientDao.findByUsername(loginValue.username)
        checkNotNull(client) { OnePieceExceptionCode.LOGIN_FAIL }
        check(loginValue.password == client.password) { OnePieceExceptionCode.LOGIN_FAIL }
        check(client.status == Status.Normal) { OnePieceExceptionCode.USER_STOP }

        // update client
        val clientUo = ClientUo(id = client.id, ip = loginValue.ip, loginTime = LocalDateTime.now(), name = null)
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

        // create own balance
        val balanceCo = BalanceCo(clientId = id)
        balanceService.create(balanceCo)

    }

    override fun update(clientUo: ClientUo) {
        val state = clientDao.update(clientUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

//    override fun updateEarnestBalance(id: Int, earnestBalance: BigDecimal) {
//        val state =  this.tryUpdateEarnestBalance(index = 0, id = id, earnestBalance = earnestBalance)
//        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
//    }
//
//    private fun tryUpdateEarnestBalance(index: Int, id: Int, earnestBalance: BigDecimal): Boolean {
//
//        if (index >= 3 ) return false
//
//        val client = clientDao.get(id)
//        val state = clientDao.updateEarnestBalance(id = id, earnestBalance = earnestBalance, processId = client.processId)
//
//        if (!state) {
//            return tryUpdateEarnestBalance(index = index + 1, id = id, earnestBalance = earnestBalance)
//        }
//
//        return state
//    }

}