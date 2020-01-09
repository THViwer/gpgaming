package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.core.dao.BalanceDao
import com.onepiece.gpgaming.beans.value.database.BalanceCo
import com.onepiece.gpgaming.beans.value.database.BalanceUo
import com.onepiece.gpgaming.beans.model.Balance
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet

@Repository
class BalanceDaoImpl : BasicDaoImpl<Balance>("balance"), BalanceDao {

    override val mapper: (rs: ResultSet) -> Balance
        get() = {rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val balance = rs.getBigDecimal("balance")
            val totalBalance = rs.getBigDecimal("total_balance")
            val giftBalance = rs.getBigDecimal("gift_balance")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val status = rs.getString("status").let { Status.valueOf(it) }
            Balance(id = id, clientId = clientId, balance = balance, totalBalance = totalBalance, giftBalance = giftBalance,
                    createdTime = createdTime, status = status)
        }

    override fun getClientBalance(clientId: Int): Balance {
        return query().where("client_id", clientId).executeOnlyOne(mapper)
    }

    override fun create(balanceCo: BalanceCo): Boolean {
        return insert().set("client_id", balanceCo.clientId)
                .set("balance", BigDecimal.ZERO)
                .set("total_balance", BigDecimal.ZERO)
                .set("gift_balance", BigDecimal.ZERO)
                .executeOnlyOne()
    }

    override fun update(balanceUo: BalanceUo): Boolean {
        val sql = "update balance set balance = balance + ${balanceUo.money} where client_id = ?"
        return jdbcTemplate.update(sql, balanceUo.clientId) == 1
    }
}