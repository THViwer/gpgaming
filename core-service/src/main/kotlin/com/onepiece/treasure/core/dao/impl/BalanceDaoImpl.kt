package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.BalanceDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.core.dao.value.BalanceCo
import com.onepiece.treasure.core.dao.value.BalanceUo
import com.onepiece.treasure.core.model.Balance
import com.onepiece.treasure.utils.JdbcBuilder
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet

@Repository
class BalanceDaoImpl : BasicDaoImpl<Balance>("balance"), BalanceDao {

    override fun mapper(): (rs: ResultSet) -> Balance {
        return { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val balance = rs.getBigDecimal("balance")
            val totalBalance = rs.getBigDecimal("total_balance")
            val giftBalance = rs.getBigDecimal("gift_balance")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            Balance(id = id, clientId = clientId, balance = balance, totalBalance = totalBalance, giftBalance = giftBalance,
                    createdTime = createdTime)
        }
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