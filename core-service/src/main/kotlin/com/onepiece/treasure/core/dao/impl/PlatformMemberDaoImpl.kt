package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.PlatformMember
import com.onepiece.treasure.beans.value.database.PlatformMemberCo
import com.onepiece.treasure.beans.value.database.PlatformMemberTransferUo
import com.onepiece.treasure.beans.value.order.BetCacheVo
import com.onepiece.treasure.core.dao.PlatformMemberDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.core.dao.basic.getIntOrNull
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet

@Repository
class PlatformMemberDaoImpl : BasicDaoImpl<PlatformMember>("platform_member"), PlatformMemberDao {

    override val mapper: (rs: ResultSet) -> PlatformMember
        get() = { rs ->

            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val memberId = rs.getInt("member_id")
            val username = rs.getString("username")
            val password = rs.getString("password")
            val totalBet = rs.getBigDecimal("total_bet")
            val totalAmount = rs.getBigDecimal("total_amount")
            val totalPromotionAmount = rs.getBigDecimal("total_promotion_amount")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            val joinPromotionId = rs.getIntOrNull("join_promotion_id")
            val currentBet = rs.getBigDecimal("current_bet")
            val requirementBet = rs.getBigDecimal("requirement_bet")
            val promotionAmount = rs.getBigDecimal("promotion_amount")
            val transferAmount = rs.getBigDecimal("transfer_amount")
            val requirementTransferOutAmount = rs.getBigDecimal("requirement_transfer_out_amount")
            val ignoreTransferOutAmount = rs.getBigDecimal("ignore_transfer_out_amount")

            PlatformMember(id = id, platform = platform, memberId = memberId, username = username, password = password,
                    currentBet = currentBet,  totalBet = totalBet, totalAmount = totalAmount, totalPromotionAmount = totalPromotionAmount,
                    createdTime = createdTime, clientId = clientId, joinPromotionId = joinPromotionId, promotionAmount = promotionAmount,
                    transferAmount = transferAmount, requirementTransferOutAmount = requirementTransferOutAmount, requirementBet = requirementBet,
                    ignoreTransferOutAmount = ignoreTransferOutAmount)
        }

    override fun findPlatformMember(memberId: Int): List<PlatformMember> {
        return query().where("member_id", memberId)
                .execute(mapper)

    }

    override fun create(platformMemberCo: PlatformMemberCo): Int {
        return insert().set("platform", platformMemberCo.platform)
                .set("client_id", platformMemberCo.clientId)
                .set("member_id", platformMemberCo.memberId)
                .set("username", platformMemberCo.username)
                .set("password", platformMemberCo.password)
                .executeGeneratedKey()
    }

    override fun transferIn(platformMemberTransferUo: PlatformMemberTransferUo): Boolean {
        return update()
                .set("promotion_amount", platformMemberTransferUo.promotionAmount)
                .set("join_promotion_id", platformMemberTransferUo.joinPromotionId)
                .set("transfer_amount", platformMemberTransferUo.transferAmount)
                .set("requirement_transfer_out_amount", platformMemberTransferUo.requirementTransferOutAmount)
                .set("ignore_transfer_out_amount", platformMemberTransferUo.ignoreTransferOutAmount)
                .set("requirement_bet", platformMemberTransferUo.requirementBet)
                .set("current_bet", BigDecimal.ZERO)
                .asSet("total_promotion_amount = total_promotion_amount + ${platformMemberTransferUo.promotionAmount}")
                .asSet("total_amount = total_amount + ${platformMemberTransferUo.transferAmount}")
                .where("id", platformMemberTransferUo.id)
                .executeOnlyOne()
    }

    override fun cleanTransferIn(memberId: Int, platform: Platform): Boolean {
        return update()
                .set("current_bet", BigDecimal.ZERO)
                .set("requirement_bet", BigDecimal.ZERO)
                .set("promotion_amount", BigDecimal.ZERO)
                .set("transfer_amount", BigDecimal.ZERO)
                .set("requirement_transfer_out_amount", BigDecimal.ZERO)
                .set("ignore_transfer_out_amount", BigDecimal.ZERO)
                .asSet("join_promotion_id = null")
                .where("member_id", memberId)
                .where("platform", platform)
                .executeOnlyOne()
    }

    override fun batchBet(data: List<BetCacheVo>) {
        val sqls = data.map {
            "update platform_member set current_bet = current_bet + ${it.bet}, total_bet = total_bet + ${it.bet} where member_id = ${it.memberId} and platform = '${it.platform.name}'"
        }
        jdbcTemplate.batchUpdate(*sqls.toTypedArray())

    }
}