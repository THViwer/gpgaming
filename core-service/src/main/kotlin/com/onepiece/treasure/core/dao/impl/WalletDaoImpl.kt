package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.model.Wallet
import com.onepiece.treasure.beans.value.database.*
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

            val totalDepositBalance = rs.getBigDecimal("total_deposit_balance")
            val totalWithdrawBalance = rs.getBigDecimal("total_withdraw_balance")
            val totalGiftBalance = rs.getBigDecimal("total_gift_balance")
            val totalDepositFrequency = rs.getInt("total_deposit_frequency")
            val totalWithdrawFrequency = rs.getInt("total_withdraw_frequency")
            val totalTransferInFrequency = rs.getInt("total_transfer_in_frequency")
            val totalTransferOutFrequency = rs.getInt("total_transfer_out_frequency")

            val processId = rs.getString("process_id")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            Wallet(id = id, clientId = clientId, memberId = memberId, balance = balance, totalDepositBalance = totalDepositBalance,
                    totalDepositFrequency = totalDepositFrequency, createdTime = createdTime,
                    processId = processId, freezeBalance = freezeBalance, totalWithdrawFrequency = totalWithdrawFrequency,
                    totalGiftBalance = totalGiftBalance, totalWithdrawBalance = totalWithdrawBalance,
                    totalTransferInFrequency = totalTransferInFrequency, totalTransferOutFrequency = totalTransferOutFrequency)
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

//    override fun update(walletUo: WalletUo): Boolean {
//        return update().asSet("balance = balance + ${walletUo.money}")
//                .asSet("freeze_balance = freeze_balance + ${walletUo.freezeMoney}")
//                .asSet("total_gift_balance = total_gift_balance + ${walletUo.giftMoney}")
//                .set("process_id", UUID.randomUUID().toString())
//                .where("id", walletUo.id)
//                .where("process_id", walletUo.processId)
//                .executeOnlyOne()
//    }
//
//    override fun transfer(walletUo: WalletUo): Boolean {
//        return update().set("balance", walletUo.memberId)
//                .asSet("total_balance = total_balance + ${walletUo.money}")
//                .set("process_id", UUID.randomUUID().toString())
//                .where("id", walletUo.id)
//                .where("process_id", walletUo.processId)
//                .executeOnlyOne()
//    }

    override fun deposit(walletDepositUo: WalletDepositUo): Boolean {
        return update().asSet("balance = balance + ${walletDepositUo.money}")
                .asSet("total_deposit_balance = total_deposit_balance + ${walletDepositUo.money}")
                .asSet("total_deposit_frequency = total_deposit_frequency + 1")
                .set("process_id", UUID.randomUUID().toString())
                .where("id", walletDepositUo.id)
                .where("process_id", walletDepositUo.processId)
                .executeOnlyOne()
    }

    override fun freeze(walletFreezeUo: WalletFreezeUo): Boolean {
        return update().asSet("balance = balance - ${walletFreezeUo.money}")
                .asSet("freeze_balance = freeze_balance + ${walletFreezeUo.money}")
                .set("process_id", UUID.randomUUID().toString())
                .where("id", walletFreezeUo.id)
                .where("process_id", walletFreezeUo.processId)
                .asWhere("balance >= ${walletFreezeUo.money}")
                .executeOnlyOne()
    }

    override fun withdraw(walletWithdrawUo: WalletWithdrawUo): Boolean {
        return update().asSet("freeze_balance = freeze_balance - ${walletWithdrawUo.money}")
                .asSet("total_withdraw_frequency = total_withdraw_frequency + 1")
                .asSet("total_withdraw_balance = total_withdraw_balance + ${walletWithdrawUo.money}")
                .set("process_id", UUID.randomUUID().toString())
                .where("id", walletWithdrawUo.id)
                .where("process_id", walletWithdrawUo.processId)
                .executeOnlyOne()
    }

    override fun withdrawFail(walletWithdrawUo: WalletWithdrawUo): Boolean {
        return update().asSet("freeze_balance = freeze_balance - ${walletWithdrawUo.money}")
                .asSet("balance = balance + ${walletWithdrawUo.money}")
                .set("process_id", UUID.randomUUID().toString())
                .where("id", walletWithdrawUo.id)
                .where("process_id", walletWithdrawUo.processId)
                .executeOnlyOne()    }

    override fun transferIn(walletTransferInUo: WalletTransferInUo): Boolean {
        return update().asSet("balance = balance + ${walletTransferInUo.money}")
                .set("process_id", UUID.randomUUID().toString())
                .asSet("total_transfer_in_frequency = total_transfer_in_frequency + 1")
                .where("id", walletTransferInUo.id)
                .where("process_id", walletTransferInUo.processId)
                .executeOnlyOne()
    }

    override fun transferOut(walletTransferOutUo: WalletTransferOutUo, frequency: Int): Boolean {

        val frequencyStr = if (frequency > 0) " + 1" else " - 1"

        return update().asSet("balance = balance - ${walletTransferOutUo.money}")
                .asSet("total_gift_balance = total_gift_balance + ${walletTransferOutUo.giftMoney}")
                .set("process_id", UUID.randomUUID().toString())
                .asSet("total_transfer_out_frequency = total_transfer_out_frequency $frequencyStr")
                .where("id", walletTransferOutUo.id)
                .where("process_id", walletTransferOutUo.processId)
                .asWhere("balance >= ${walletTransferOutUo.money}")
                .executeOnlyOne()
    }

    override fun query(walletQuery: WalletQuery): List<Wallet> {
        return query()
                .where("client_id", walletQuery.clientId)
                .where("member_id", walletQuery.memberId)
                .asWhere("balance >= ?", walletQuery.minBalance)
                .asWhere("balance < ?", walletQuery.maxBalance)
                .asWhere("total_deposit_balance >= ?", walletQuery.minTotalDepositBalance)
                .asWhere("total_deposit_balance < ?", walletQuery.maxTotalDepositBalance)
                .asWhere("total_withdraw_balance >= ?", walletQuery.minTotalWithdrawBalance)
                .asWhere("total_withdraw_balance < ?", walletQuery.maxTotalWithdrawBalance)
                .asWhere("total_deposit_frequency >= ?", walletQuery.minTotalDepositFrequency)
                .asWhere("total_deposit_frequency < ?", walletQuery.maxTotalDepositFrequency)
                .asWhere("total_withdraw_frequency >= ?", walletQuery.minTotalWithdrawFrequency)
                .asWhere("total_withdraw_frequency < ?", walletQuery.maxTotalWithdrawFrequency)
                .execute(mapper)


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