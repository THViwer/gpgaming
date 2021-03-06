package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.DepositState
import com.onepiece.gpgaming.beans.enums.PayState
import com.onepiece.gpgaming.beans.enums.PayType
import com.onepiece.gpgaming.beans.model.PayOrder
import com.onepiece.gpgaming.beans.value.database.PayOrderValue
import com.onepiece.gpgaming.core.dao.PayOrderDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import com.onepiece.gpgaming.core.dao.basic.getIntOrNull
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class PayOrderDaoImpl : BasicDaoImpl<PayOrder>("pay_order"), PayOrderDao {

    override val mapper: (rs: ResultSet) -> PayOrder
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val username = rs.getString("username")
            val payId = rs.getInt("pay_id")
            val payType = rs.getString("pay_type").let { PayType.valueOf(it) }
            val bank = rs.getString("bank")?.let { Bank.valueOf(it) }
            val amount = rs.getBigDecimal("amount")
            val orderId = rs.getString("order_id")
            val thirdOrderId = rs.getString("third_order_id")
            val operatorId = rs.getIntOrNull("operator_id")
            val operatorUsername = rs.getString("operator_username")
            val remark = rs.getString("remark")
            val state = rs.getString("state").let { PayState.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val updatedTime = rs.getTimestamp("updated_time").toLocalDateTime()

            PayOrder(id = id, clientId = clientId, memberId = memberId, username = username, amount = amount,
                    orderId = orderId, thirdOrderId = thirdOrderId, operatorId = operatorId, operatorUsername = operatorUsername,
                    remark = remark, state = state, createdTime = createdTime, updatedTime = updatedTime, payType = payType,
                    payId = payId, bank = bank)
        }

    override fun summary(query: PayOrderValue.PayOrderQuery): List<PayOrderValue.ThirdPaySummary> {
        return query("bank, state, sum(amount) total_amount")
                .where("client_id", query.clientId)
                .asWhere("created_time >= ?", query.startDate)
                .asWhere("created_time < ?", query.endDate)
                .where("pay_type", query.payType)
                .where("member_id", query.memberId)
                .whereIn("member_id", query.memberIds)
                .where("username", query.username)
                .where("state", query.state)
                .asWhere("bank is not null")
                .group("bank, state")
                .execute { rs ->
                    val bank = rs.getString("bank").let { Bank.valueOf(it) }
                    val state = rs.getString("state").let { PayState.valueOf(it) }
                    val totalAmount = rs.getBigDecimal("total_amount")

                    PayOrderValue.ThirdPaySummary(bank = bank, totalAmount = totalAmount, state = state)
                }
    }

    override fun total(query: PayOrderValue.PayOrderQuery): Int {
        return query("count(*)")
                .where("client_id", query.clientId)
                .asWhere("created_time >= ?", query.startDate)
                .asWhere("created_time < ?", query.endDate)
                .where("pay_type", query.payType)
                .where("member_id", query.memberId)
                .whereIn("member_id", query.memberIds)
                .where("username", query.username)
                .where("state", query.state)
                .count()
    }

    override fun query(query: PayOrderValue.PayOrderQuery): List<PayOrder> {
        return query()
                .where("client_id", query.clientId)
                .asWhere("created_time >= ?", query.startDate)
                .asWhere("created_time < ?", query.endDate)
                .where("pay_type", query.payType)
                .where("member_id", query.memberId)
                .whereIn("member_id", query.memberIds)
                .where("username", query.username)
                .where("state", query.state)
                .sort(query.sortBy)
                .limit(query.current, query.size)
                .execute(mapper)
    }

    override fun find(orderId: String): PayOrder {
        return query()
                .where("order_id", orderId)
                .executeOnlyOne(mapper)
    }

    override fun create(co: PayOrderValue.PayOrderCo): Boolean {
        return insert()
                .set("client_id", co.clientId)
                .set("member_id", co.memberId)
                .set("username", co.username)
                .set("order_id", co.orderId)
                .set("amount", co.amount)
                .set("state", PayState.Process)
                .set("pay_id", co.payId)
                .set("pay_type", co.payType)
                .set("bank", co.bank)
                .executeOnlyOne()
    }

    override fun check(uo: PayOrderValue.ConstraintUo): Boolean {

        return update()
                .set("operator_id", uo.operatorId)
                .set("operator_username", uo.operatorUsername)
                .set("remark", uo.remark)
                .set("updated_time", LocalDateTime.now())
                .set("state", PayState.Successful)
                .where("order_id", uo.orderId)
                .executeOnlyOne()

    }

    override fun successful(orderId: String, thirdOrderId: String): Boolean {
        return update()
                .set("state", PayState.Successful)
                .set("updated_time", LocalDateTime.now())
                .set("third_order_id", thirdOrderId)
                .where("order_id", orderId)
                .asWhere("state != ?", PayState.Successful)
                .executeOnlyOne()
    }

    override fun failed(orderId: String): Boolean {
        return update()
                .set("state", PayState.Failed)
                .where("order_id", orderId)
                .asWhere("state != '${PayState.Successful.name}'")
                .executeOnlyOne()
    }

    override fun close(closeTime: LocalDateTime) {
        val num = update()
                .set("state", PayState.Close)
                .set("updated_time", LocalDateTime.now())
                .asWhere("created_time < ?", closeTime)
                .where("state", PayState.Process)
                .execute()
        println(num)
    }

    override fun mReport(clientId: Int?, startDate: LocalDate, endDate: LocalDate, memberId: Int?, memberIds: List<Int>?): List<PayOrderValue.PayOrderMReport> {
        return query("client_id, member_id, sum(amount) as amount, count(*) as count")
                .asWhere("updated_time >= ?", startDate)
                .asWhere("updated_time < ?", endDate)
                .where("client_id", clientId)
                .where("state", PayState.Successful)
                .where("member_id", memberId)
                .whereIn("member", memberIds)
                .group("client_id, member_id")
                .execute { rs ->

                    val mClientId = rs.getInt("client_id")
                    val mMemberId = rs.getInt("member_id")
                    val amount = rs.getBigDecimal("amount")
                    val count = rs.getInt("count")

                    PayOrderValue.PayOrderMReport(clientId = mClientId, memberId = mMemberId, totalAmount = amount, count = count)
                }
    }

    override fun cpReport(startDate: LocalDate, constraint: Boolean): List<PayOrderValue.PayOrderCPReport> {

        return query("client_id, pay_type, sum(amount)")
                .asWhere("updated_time >= ?", startDate)
                .asWhere("updated_time < ?", startDate.plusDays(1))
                .asWhere("operator_id != null", constraint)
                .where("state", PayState.Successful)
                .group("client_id, pay_type")
                .execute { rs ->
                    val clientId = rs.getInt("client_id")
                    val payType = rs.getString("pay_type").let { PayType.valueOf(it) }
                    val amount = rs.getBigDecimal("amount")

                    PayOrderValue.PayOrderCPReport(clientId = clientId, payType = payType, totalAmount = amount)

                }
    }

    override fun cReport(startDate: LocalDate, constraint: Boolean): List<PayOrderValue.PayOrderCReport> {
        return query("client_id, sum(amount) as amount, count(*) as count, count(distinct member_id) as thirdPaySequence")
                .asWhere("updated_time >= ?", startDate)
                .asWhere("updated_time < ?", startDate.plusDays(1))
                .asWhere("operator_id != null", constraint)
                .where("state", PayState.Successful)
                .group("client_id")
                .execute { rs ->
                    val clientId = rs.getInt("client_id")
//                    val payType  =  rs.getString("pay_type").let { PayType.valueOf(it) }
                    val amount = rs.getBigDecimal("amount")
                    val count = rs.getInt("count")
                    val thirdPaySequence = rs.getInt("thirdPaySequence")

                    PayOrderValue.PayOrderCReport(clientId = clientId, totalAmount = amount, count = count, thirdPaySequence = thirdPaySequence)

                }
    }

    override fun sumSuccessful(clientId: Int, memberId: Int, startDate: LocalDate, endDate: LocalDate): BigDecimal {
        return query("sum(amount) deposit_amount")
                .where("client_id", clientId)
                .where("member_id", memberId)
                .where("state", PayState.Successful)
                .asWhere("created_time >= ?", startDate)
                .asWhere("created_time < ?", endDate)
                .executeMaybeOne { rs ->
                    rs.getBigDecimal("deposit_amount")
                } ?: BigDecimal.ZERO
    }

    override fun delOldOrder(startDate: LocalDate) {
        val sql = "delete  from pay_order  where  created_time < ? and `state`  != 'Successful'"
        jdbcTemplate.update(sql, startDate)
    }

}