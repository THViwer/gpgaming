package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.MemberInfo
import com.onepiece.gpgaming.beans.value.database.MemberInfoValue
import com.onepiece.gpgaming.core.dao.MemberInfoDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class MemberInfoDaoImpl: BasicDaoImpl<MemberInfo>("member_info"), MemberInfoDao {

    override val mapper: (rs: ResultSet) -> MemberInfo
        get() = {  rs ->

            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val agentId = rs.getInt("agent_id")
            val saleId = rs.getInt("sale_id")
            val memberId = rs.getInt("member_id")
            val username = rs.getString("username")

            val totalDeposit = rs.getBigDecimal("total_deposit")
            val lastDepositTime = rs.getTimestamp("last_deposit_time")?.toLocalDateTime()
            val totalDepositCount = rs.getInt("total_deposit_count")



            val totalWithdraw = rs.getBigDecimal("total_withdraw")
            val lastWithdrawTime = rs.getTimestamp("last_withdraw_time")?.toLocalDateTime()
            val totalWithdrawCount = rs.getInt("total_withdraw_count")


            val registerTime = rs.getTimestamp("register_time").toLocalDateTime()
            val lastLoginTime = rs.getTimestamp("last_login_time")?.toLocalDateTime()
            val loginCount = rs.getInt("login_count")


            val lastSaleTime = rs.getTimestamp("last_sale_time")?.toLocalDateTime()
            val saleCount = rs.getInt("sale_count")


            MemberInfo(bossId = bossId, clientId = clientId, agentId = agentId, saleId = saleId, memberId = memberId, username = username,
                    totalDeposit = totalDeposit, lastDepositTime = lastDepositTime, totalDepositCount = totalDepositCount,
                    totalWithdraw = totalWithdraw, lastWithdrawTime = lastWithdrawTime, totalWithdrawCount = totalWithdrawCount,
                    registerTime = registerTime, lastLoginTime = lastLoginTime, loginCount = loginCount,
                    lastSaleTime = lastSaleTime, saleCount = saleCount)
        }


    override fun has(memberId: Int): MemberInfo? {
        return query()
                .where("member_id", memberId)
                .executeMaybeOne(mapper)
    }

    override fun create(co: MemberInfoValue.MemberInfoCo): Boolean {
        return insert()
                .set("boss_id", co.bossId)
                .set("client_id", co.clientId)
                .set("agent_id", co.agentId)
                .set("sale_id", co.saleId)
                .set("member_id", co.memberId)
                .set("username", co.username)
                .set("register_time", co.registerTime)
                .executeOnlyOne()
    }

    override fun update(uo: MemberInfoValue.MemberInfoUo): Boolean {
        return update()
                .set("sale_id", uo.saleId)

                .asSet("total_deposit = total_deposit + ${uo.deposit}")
                .set("last_deposit_time", uo.depositTime)
                .asSet("total_deposit_count = total_deposit_count + ${uo.depositCount}")

                .asSet("total_withdraw = total_withdraw + ${uo.withdraw}")
                .set("last_deposit_time", uo.withdrawTime)
                .asSet("total_withdraw_count = total_withdraw_count + ${uo.withdrawCount}")


                .set("last_login_time", uo.lastLoginTime)
                .asSet("login_count = login_count + ${uo.loginCount}")

                .set("last_sale_time", uo.lastSaleTime)
                .asSet("sale_count = sale_count + ${uo.saleCount}")


                .where("member_id", uo.memberId)
                .executeOnlyOne()
    }

    override fun list(query: MemberInfoValue.MemberInfoQuery): List<MemberInfo> {
        return query()
                .where("boss_id", query.bossId)
                .where("client_id", query.clientId)
                .where("sale_id", query.saleId)
                .where("member_id", query.memberId)

                .asWhere("total_deposit >= ?", query.totalDepositMin)
                .asWhere("total_deposit <= ?", query.totalDepositMax)
                .asWhere("total_deposit_time >= ?", query.lastDepositTimeMin)
                .asWhere("total_deposit_time <= ?", query.lastDepositTimeMax)

                .asWhere("register_time >= ?", query.registerTimeMin)
                .asWhere("register_time <= ?", query.registerTimeMax)

                .asWhere("last_login_time >= ?", query.lastLoginTimeMin)
                .asWhere("last_login_time <= ?", query.lastLoginTimeMax)
                .asWhere("login_count >= ?", query.loginCountMin)
                .asWhere("login_count <= ?", query.loginCountMax)

                .asWhere("last_sale_time >= ?", query.lastSaleTimeMin)
                .asWhere("last_sale_time <= ?", query.lastSaleTimeMax)
                .asWhere("sale_count >= ?", query.saleCountMin)
                .asWhere("sale_count <= ?", query.saleCountMax)
                .execute(mapper)
    }
}