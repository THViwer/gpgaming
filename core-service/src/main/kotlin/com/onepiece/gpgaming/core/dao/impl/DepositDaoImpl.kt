package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.DepositChannel
import com.onepiece.gpgaming.beans.enums.DepositState
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.enums.WithdrawState
import com.onepiece.gpgaming.beans.model.Deposit
import com.onepiece.gpgaming.beans.value.database.ClientDepositReportVo
import com.onepiece.gpgaming.beans.value.database.DepositCo
import com.onepiece.gpgaming.beans.value.database.DepositLockUo
import com.onepiece.gpgaming.beans.value.database.DepositQuery
import com.onepiece.gpgaming.beans.value.database.DepositReportVo
import com.onepiece.gpgaming.beans.value.database.DepositUo
import com.onepiece.gpgaming.core.dao.DepositDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import com.onepiece.gpgaming.core.dao.basic.getIntOrNull
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Repository
class DepositDaoImpl : BasicDaoImpl<Deposit>("deposit"), DepositDao {

    override val mapper: (rs: ResultSet) -> Deposit
        get() = { rs ->
            val id = rs.getInt("id")
            val orderId = rs.getString("order_id")
            val processId = rs.getString("process_id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val username = rs.getString("username")
            val memberBankId = rs.getInt("member_bank_id")
            val memberName = rs.getString("member_name")
            val memberBank = rs.getString("member_bank").let { Bank.valueOf(it) }
            val memberBankCardNumber = rs.getString("member_bank_card_number")
            val clientBankId = rs.getInt("client_bank_id")
            val clientBank = rs.getString("client_bank").let { Bank.valueOf(it) }
            val clientBankCardNumber = rs.getString("client_bank_card_number")
            val clientBankName = rs.getString("client_bank_name")
            val money = rs.getBigDecimal("money")
            val depositTime = rs.getTimestamp("deposit_time").toLocalDateTime()
            val channel = rs.getString("channel").let { DepositChannel.valueOf(it) }
            val imgPath = rs.getString("img_path")
            val state = rs.getString("state").let { DepositState.valueOf(it) }
            val remarks = rs.getString("remarks")
            val lockWaiterId = rs.getIntOrNull("lock_waiter_id")
            val lockWaiterName = rs.getString("lock_waiter_name")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val endTime = rs.getTimestamp("end_time")?.toLocalDateTime()
            val status = rs.getString("status").let { Status.valueOf(it) }

            Deposit(id = id, orderId = orderId, clientId = clientId, memberId = memberId, memberBank = memberBank, money = money,
                    imgPath = imgPath, state = state, remarks = remarks, createdTime = createdTime, endTime = endTime,
                    memberBankCardNumber = memberBankCardNumber, processId = processId, memberName = memberName, clientBankId = clientBankId,
                    clientBankName = clientBankName, clientBankCardNumber = clientBankCardNumber, lockWaiterId = lockWaiterId,
                    lockWaiterName = lockWaiterName, depositTime = depositTime, channel = channel, username = username, memberBankId = memberBankId,
                    clientBank = clientBank, status = status)
        }

    override fun findDeposit(clientId: Int, orderId: String): Deposit {
        return query().where("client_id", clientId)
                .where("order_id", orderId)
                .executeOnlyOne(mapper)
    }

    override fun query(query: DepositQuery, current: Int, size: Int): List<Deposit> {
        val builder = query().where("client_id", query.clientId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .where("member_id", query.memberId)
                .where("order_id", query.orderId)
                .where("state", query.state)
                .whereIn("client_bank_id", query.clientBankIdList)

        if (query.lockWaiterId != null) {
            builder.asWhere("(lock_waiter_id is null || lock_waiter_id = ${query.lockWaiterId})")
        }



        return builder.sort("id desc")
                .limit(current, size)
                .execute(mapper)
    }

    override fun total(query: DepositQuery): Int {
        return query(" count(*) as count")
                .where("client_id", query.clientId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .where("member_id", query.memberId)
                .where("order_id", query.orderId)
                .where("state", query.state)
                .count()
    }

    override fun lock(depositLockUo: DepositLockUo): Boolean {
        return update().set("lock_waiter_id", depositLockUo.lockWaiterId)
                .set("lock_waiter_name", depositLockUo.lockWaiterName)
                .set("process_id", UUID.randomUUID().toString())
                .where("client_id", depositLockUo.clientId)
                .where("order_id", depositLockUo.orderId)
                .where("process_id", depositLockUo.processId)
                .asWhere("lock_waiter_id is null")
                .executeOnlyOne()
    }

    override fun create(depositCo: DepositCo): Boolean {
        return insert().set("order_id", depositCo.orderId)
                .set("process_id", UUID.randomUUID().toString())
                .set("client_id", depositCo.clientId)
                .set("member_id", depositCo.memberId)
                .set("username", depositCo.username)
                .set("member_bank_id", depositCo.memberBankId)
                .set("member_name", depositCo.memberName)
                .set("member_bank", depositCo.memberBank)
                .set("member_bank_card_number", depositCo.memberBankCardNumber)
                .set("client_bank_id", depositCo.clientBankId)
                .set("client_bank", depositCo.clientBank)
                .set("client_bank_name", depositCo.clientBankName)
                .set("client_bank_card_number", depositCo.clientBankCardNumber)
                .set("money", depositCo.money)
                .set("deposit_time", depositCo.depositTime)
                .set("channel", depositCo.channel)
                .set("img_path", depositCo.imgPath)
                .set("state", DepositState.Process)
                .executeOnlyOne()
    }

    override fun update(depositUo: DepositUo): Boolean {
        return update().set("state", depositUo.state)
                .set("remarks", depositUo.remarks)
                .set("process_id", UUID.randomUUID().toString())
                .set("end_time", LocalDateTime.now())
                .where("order_id", depositUo.orderId)
                .where("process_id", depositUo.processId)
                .where("lock_waiter_id", depositUo.lockWaiterId)
                .executeOnlyOne()
    }

    override fun report(clientId: Int?, memberId: Int?, startDate: LocalDate, endDate: LocalDate, memberIds: List<Int>?): List<DepositReportVo> {
        return query("client_id, member_id, sum(money) as money, count(money) as count")
                .where("state", DepositState.Successful)
                .where("client_id", clientId)
                .where("member_id", memberId)
                .whereIn("member_id", memberIds)
                .asWhere("end_time >= ?", startDate)
                .asWhere("end_time < ?", endDate)
                .group("client_id, member_id")
                .execute { rs ->
                    val xClientId = rs.getInt("client_id")
                    val xMemberId = rs.getInt("member_id")
                    val money = rs.getBigDecimal("money")
                    val count = rs.getInt("count")
                    DepositReportVo(clientId = xClientId, memberId = xMemberId, money = money, count = count)
                }
    }

    override fun sumSuccessful(clientId: Int, memberId: Int, startDate: LocalDate, endDate: LocalDate): BigDecimal {
        return query("sum(money) deposit_amount")
                .where("client_id", clientId)
                .where("member_id", memberId)
                .where("state", DepositState.Successful)
                .asWhere("created_time >= ?", startDate)
                .asWhere("created_time < ?", endDate)
                .executeMaybeOne { rs ->
                    rs.getBigDecimal("deposit_amount")
                } ?: BigDecimal.ZERO
    }

    override fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientDepositReportVo> {
        return query("client_id, sum(money) as money, count(client_id) as count, count(distinct member_id) as depositSequence")
                .where("state", WithdrawState.Successful)
                .asWhere("end_time >= ?", startDate)
                .asWhere("end_time < ?", endDate)
                .group("client_id")
                .execute { rs ->
                    val clientId = rs.getInt("client_id")
                    val count = rs.getInt("count")
                    val money = rs.getBigDecimal("money")
                    val depositSequence = rs.getInt("depositSequence")
                    ClientDepositReportVo(clientId = clientId, count = count, money = money, depositSequence = depositSequence)
                }
    }

    override fun delOldOrder(startDate: LocalDate) {
        val sql = "delete  from deposit  where  created_time > ? and `state`  != 'Successful'"
        jdbcTemplate.update(sql, startDate)
    }
}