package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.model.Wallet
import com.onepiece.treasure.beans.value.database.WalletCo
import com.onepiece.treasure.beans.value.database.WalletUo
import com.onepiece.treasure.core.dao.WalletDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

@Repository
class WalletDaoImpl : BasicDaoImpl<Wallet>("wallet"), WalletDao {

    override val mapper: (rs: ResultSet) -> Wallet
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val balance = rs.getBigDecimal("balance")
            val freezeBalance = rs.getBigDecimal("freeze_balance")

            val totalBalance = rs.getBigDecimal("total_balance")
            val totalGiftBalance = rs.getBigDecimal("total_gift_balance")
            val totalDepositFrequency = rs.getInt("total_deposit_frequency")
            val totalWithdrawFrequency = rs.getInt("total_withdraw_frequency")

            val processId = rs.getString("process_id")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            Wallet(id = id, clientId = clientId, memberId = memberId, balance = balance, totalBalance = totalBalance,
                    totalDepositFrequency = totalDepositFrequency, createdTime = createdTime,
                    processId = processId, freezeBalance = freezeBalance, totalWithdrawFrequency = totalWithdrawFrequency,
                    totalGiftBalance = totalGiftBalance)
        }

    override fun getMemberWallet(memberId: Int): Wallet {
        return query().where("member_id", memberId)
                .executeOnlyOne(mapper)
    }

    override fun create(walletCo: WalletCo): Boolean {
        return insert().set("client_id", walletCo.clientId)
                .set("member_id", walletCo.memberId)
                .executeOnlyOne()
    }

    override fun update(walletUo: WalletUo): Boolean {
        return update().asSet("balance = balance + ${walletUo.money}")
                .asSet("freeze_balance = freeze_balance + ${walletUo.freezeMoney}")
                .asSet("total_gift_balance = total_gift_balance + ${walletUo.giftMoney}")
                .set("process_id", UUID.randomUUID().toString())
                .where("id", walletUo.id)
                .where("process_id", walletUo.processId)
                .executeOnlyOne()
    }

    override fun transfer(walletUo: WalletUo): Boolean {
        return update().set("balance", walletUo.memberId)
                .asSet("total_balance = total_balance + ${walletUo.money}")
                .set("process_id", UUID.randomUUID().toString())
                .where("id", walletUo.id)
                .where("process_id", walletUo.processId)
                .executeOnlyOne()

    }

//    override fun bet(walletUo: WalletUo): Boolean {
//        return update().asSet("balance = balance + ${walletUo.money}")
//                .asSet("current_bet = current_bet + ${walletUo.bet}")
//                .asSet("total_bet = total_bet + ${walletUo.bet}")
//                .set("process_id", UUID.randomUUID().toString())
//                .where("id", walletUo.id)
//                .where("process_id", walletUo.processId)
//                .executeOnlyOne()
//    }
}