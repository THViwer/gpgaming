package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.WalletDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.beans.value.database.WalletCo
import com.onepiece.treasure.beans.value.database.WalletUo
import com.onepiece.treasure.beans.model.Wallet
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet

@Repository
class WalletDaoImpl : BasicDaoImpl<Wallet>("wallet"), WalletDao {

    override fun mapper(): (rs: ResultSet) -> Wallet {
        return { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val balance = rs.getBigDecimal("balance")
            val totalBalance = rs.getBigDecimal("total_balance")
            val totalFrequency = rs.getInt("total_frequency")
            val giftBalance = rs.getBigDecimal("gift_balance")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            Wallet(id = id, clientId = clientId, memberId = memberId, balance = balance, totalBalance = totalBalance, totalFrequency = totalFrequency,
                    giftBalance = giftBalance, createdTime = createdTime)
        }
    }

    override fun create(walletCo: WalletCo): Boolean {
        return insert().set("client_id", walletCo.clientId)
                .set("member_id", walletCo.memberId)
                .set("balance", BigDecimal.ZERO)
                .set("total_balance", BigDecimal.ZERO)
                .set("total_frequency", BigDecimal.ZERO)
                .set("gift_balance", BigDecimal.ZERO)
                .executeOnlyOne()
    }

    override fun update(walletUo: WalletUo): Boolean {
        val sql = """
            update wallet set 
                balance = balance + ${walletUo.money}, 
                total_balance = total_balance + ${walletUo.addTotalMoney}, 
                gift_balance = gift_balance = ${walletUo.giftMoney} 
            where client_id = ? and member_id = ?
            """
        return jdbcTemplate.update(sql, walletUo.clientId, walletUo.memberId) == 1
    }
}