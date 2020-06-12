package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.enums.WithdrawState
import com.onepiece.gpgaming.beans.model.Withdraw
import com.onepiece.gpgaming.beans.value.database.ClientWithdrawReportVo
import com.onepiece.gpgaming.beans.value.database.DepositLockUo
import com.onepiece.gpgaming.beans.value.database.WithdrawCo
import com.onepiece.gpgaming.beans.value.database.WithdrawQuery
import com.onepiece.gpgaming.beans.value.database.WithdrawReportVo
import com.onepiece.gpgaming.beans.value.database.WithdrawUo
import com.onepiece.gpgaming.core.dao.WithdrawDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import com.onepiece.gpgaming.core.dao.basic.getIntOrNull
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Repository
class WithdrawOrderDaoImpl : BasicDaoImpl<Withdraw>("withdraw"), WithdrawDao {

    override val mapper: (rs: ResultSet) -> Withdraw
        get() = { rs ->
            val id = rs.getInt("id")
            val orderId = rs.getString("order_id")
            val processId = rs.getString("process_id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val role = rs.getString("role").let { Role.valueOf(it) }
            val username = rs.getString("username")
            val memberBankId = rs.getInt("member_bank_id")
            val memberBank = rs.getString("member_bank").let { Bank.valueOf(it) }
            val memberName = rs.getString("member_name")
            val memberBankCardNumber = rs.getString("member_bank_card_number")
            val money = rs.getBigDecimal("money")
            val state = rs.getString("state").let { WithdrawState.valueOf(it) }
            val remarks = rs.getString("remarks")
            val lockWaiterId = rs.getIntOrNull("lock_waiter_id")
            val lockWaiterName = rs.getString("lock_waiter_name")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val endTime = rs.getTimestamp("end_time")?.toLocalDateTime()
            val status = rs.getString("status").let { Status.valueOf(it) }

            Withdraw(id = id, orderId = orderId, processId = processId, clientId = clientId, memberId = memberId,
                    memberBankId = memberBankId, money = money, state = state, remarks = remarks, createdTime = createdTime,
                    endTime = endTime, memberName = memberName, lockWaiterId = lockWaiterId, lockWaiterName = lockWaiterName,
                    memberBankCardNumber = memberBankCardNumber, memberBank = memberBank, username = username, status = status,
                    role = role)
        }

    override fun findWithdraw(clientId: Int, orderId: String): Withdraw {
        return query().where("client_id", clientId)
                .where("order_id", orderId)
                .executeOnlyOne(mapper)
    }

    override fun query(query: WithdrawQuery, current: Int, size: Int): List<Withdraw> {
        val builder = query().where("client_id", query.clientId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .where("order_id", query.orderId)
                .where("member_id", query.memberId)
                .whereIn("member_id", query.memberIds)
        if (query.lockWaiterId != null) {
            builder.asWhere("(lock_waiter_id is null || lock_waiter_id = ${query.lockWaiterId})")
        }

        return builder.where("state", query.state)
                .sort("id desc")
                .limit(current, size)
                .execute(mapper)
    }

    override fun total(query: WithdrawQuery): Int {
        return query(" count(*) as count").where("client_id", query.clientId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .where("order_id", query.orderId)
                .where("member_id", query.memberId)
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

    override fun create(orderCo: WithdrawCo): Boolean {
        return insert().set("order_id", orderCo.orderId)
                .set("process_id", UUID.randomUUID().toString())
                .set("client_id", orderCo.clientId)
                .set("member_id", orderCo.memberId)
                .set("username", orderCo.username)
                .set("member_bank_id", orderCo.memberBankId)
                .set("member_name", orderCo.memberName)
                .set("member_bank", orderCo.memberBank)
                .set("member_bank_card_number", orderCo.memberBankCardNumber)
                .set("money", orderCo.money)
                .set("state", WithdrawState.Process)
                .set("remarks", orderCo.remarks)
                .set("role", orderCo.role)
                .executeOnlyOne()
    }

    override fun check(orderUo: WithdrawUo): Boolean {
        return update()
                .set("state", orderUo.state)
                .set("process_id", UUID.randomUUID().toString())
                .set("remarks", orderUo.remarks)
                .set("end_time", LocalDateTime.now())
                .where("client_id", orderUo.clientId)
                .where("order_id", orderUo.orderId)
                .where("process_id", orderUo.processId)
                .where("lock_waiter_id", orderUo.waiterId)
                .executeOnlyOne()
    }

    override fun report(clientId: Int?, memberId: Int?, startDate: LocalDate, endDate: LocalDate): List<WithdrawReportVo> {
        return query("client_id, member_id, sum(money) as money, count(id) as count")
                .where("client_id", clientId)
                .where("member_id", memberId)
                .where("state", WithdrawState.Successful)
                .asWhere("end_time >= ?", startDate)
                .asWhere("end_time < ?", endDate)
                .group("client_id, member_id")
                .execute { rs ->
                    val xClientId = rs.getInt("client_id")
                    val xMemberId = rs.getInt("member_id")
                    val money = rs.getBigDecimal("money")
                    val count = rs.getInt("count")
                    WithdrawReportVo(clientId = xClientId, memberId = xMemberId, money = money, count = count)
                }
    }

    override fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientWithdrawReportVo> {
        return query("client_id, sum(money) as money, count(client_id) as count")
                .where("state", WithdrawState.Successful)
                .asWhere("end_time >= ?", startDate)
                .asWhere("end_time < ?", endDate)
                .group("client_id")
                .execute { rs ->
                    val clientId = rs.getInt("client_id")
                    val count = rs.getInt("count")
                    val money = rs.getBigDecimal("money")
                    ClientWithdrawReportVo(clientId = clientId, count = count, money = money)
                }
    }
}