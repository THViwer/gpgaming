package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.Wallet
import com.onepiece.treasure.beans.value.database.WalletCo
import com.onepiece.treasure.beans.value.database.WalletUo
import com.onepiece.treasure.core.dao.WalletDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.ResultSet
import java.util.*

@Repository
class WalletDaoImpl : BasicDaoImpl<Wallet>("wallet"), WalletDao {

    override val mapper: (rs: ResultSet) -> Wallet
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val balance = rs.getBigDecimal("balance")
            val freezeBalance = rs.getBigDecimal("freeze_balance")
            val currentBet = rs.getBigDecimal("current_bet")
            val demandBet = rs.getBigDecimal("demand_bet")
            val giftBalance = rs.getBigDecimal("gift_balance")

            val totalBalance = rs.getBigDecimal("total_balance")
            val totalFrequency = rs.getInt("total_frequency")
            val totalBet = rs.getBigDecimal("total_bet")
            val totalGiftBalance = rs.getBigDecimal("total_gift_balance")

            val processId = rs.getString("process_id")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            Wallet(id = id, clientId = clientId, memberId = memberId, balance = balance, totalBalance = totalBalance, totalFrequency = totalFrequency,
                    giftBalance = giftBalance, createdTime = createdTime, platform = platform, currentBet = currentBet, totalBet = totalBet,
                    totalGiftBalance = totalGiftBalance, processId = processId, demandBet = demandBet, freezeBalance = freezeBalance)
        }

    override fun getMemberWallet(memberId: Int, platform: Platform): Wallet {
        return query().where("member_id", memberId)
                .where("platform", platform)
                .executeOnlyOne(mapper)
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
        return update().asSet("balance = balance + ${walletUo.money}")
                .asSet("freeze_balance = freeze_balance + ${walletUo.freezeMoney}")
                .set("process_id", UUID.randomUUID().toString())
                .where("id", walletUo.id)
                .where("process_id", walletUo.processId)
                .executeOnlyOne()
    }

    override fun transfer(walletUo: WalletUo): Boolean {
        return update().set("balance", walletUo.memberId)
                .set("current_bet", BigDecimal.ZERO)
                .set("demand_bet", BigDecimal.ZERO)
                .set("gift_balance", walletUo.giftMoney)
                .asSet("total_balance = total_balance + ${walletUo.money}")
                .set("process_id", UUID.randomUUID().toString())
                .where("id", walletUo.id)
                .where("process_id", walletUo.processId)
                .executeOnlyOne()

    }

    override fun bet(walletUo: WalletUo): Boolean {
        return update().asSet("balance = balance + ${walletUo.money}")
                .asSet("current_bet = current_bet + ${walletUo.bet}")
                .asSet("total_bet = total_bet + ${walletUo.bet}")
                .set("process_id", UUID.randomUUID().toString())
                .where("id", walletUo.id)
                .where("process_id", walletUo.processId)
                .executeOnlyOne()
    }
}