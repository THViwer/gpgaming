package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.PlatformMember
import com.onepiece.treasure.beans.value.database.PlatformMemberBetUo
import com.onepiece.treasure.beans.value.database.PlatformMemberCo
import com.onepiece.treasure.beans.value.database.PlatformMemberTransferUo
import com.onepiece.treasure.core.dao.PlatformMemberDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet

@Repository
class PlatformMemberDaoImpl : BasicDaoImpl<PlatformMember>("platform_member"), PlatformMemberDao {

    override val mapper: (rs: ResultSet) -> PlatformMember
        get() = { rs ->

            val id = rs.getInt("id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val memberId = rs.getInt("member_id")
            val username = rs.getString("username")
            val password = rs.getString("password")
            val currentBet = rs.getBigDecimal("current_bet")
            val demandBet = rs.getBigDecimal("demand_bet")
            val giftBalance = rs.getBigDecimal("gift_balance")
            val totalBet = rs.getBigDecimal("total_bet")
            val totalBalance = rs.getBigDecimal("total_balance")
            val totalGiftBalance = rs.getBigDecimal("total_gift_balance")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            PlatformMember(id = id, platform = platform, memberId = memberId, username = username, password = password,
                    currentBet = currentBet, demandBet = demandBet, giftBalance = giftBalance, totalBet = totalBet,
                    totalBalance = totalBalance, totalGiftBalance = totalGiftBalance, createdTime = createdTime)
        }

    override fun findPlatformMember(memberId: Int): List<PlatformMember> {
        return query().where("member_id", memberId)
                .execute(mapper)

    }

    override fun create(platformMemberCo: PlatformMemberCo): Int {
        return insert().set("platform", platformMemberCo.platform)
                .set("member_id", platformMemberCo.memberId)
                .set("username", platformMemberCo.username)
                .set("password", platformMemberCo.password)
                .executeGeneratedKey()
    }

    override fun bet(platformMemberBetUo: PlatformMemberBetUo): Boolean {
        return update()
                .asSet("current_bet = current_bet + ${platformMemberBetUo.bet}")
                .asSet("total_bet = total_bet + ${platformMemberBetUo.bet}")
                .where("id", platformMemberBetUo.id)
                .executeOnlyOne()

    }

    override fun transferIn(platformMemberTransferUo: PlatformMemberTransferUo): Boolean {
        return update()
                .set("gift_balance", platformMemberTransferUo.giftBalance)
                .set("current_bet", BigDecimal.ZERO)
                .set("demand_bet", platformMemberTransferUo.demandBet)
                .asSet("total_gift_balance = total_gift_balance + ${platformMemberTransferUo.giftBalance}")
                .asSet("total_balance = total_balance + ${platformMemberTransferUo.money}")
                .where("id", platformMemberTransferUo.id)
                .executeOnlyOne()
    }

}