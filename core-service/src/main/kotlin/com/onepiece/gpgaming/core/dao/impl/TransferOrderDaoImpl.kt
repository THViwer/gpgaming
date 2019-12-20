package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.TransferState
import com.onepiece.gpgaming.beans.model.TransferOrder
import com.onepiece.gpgaming.beans.value.database.ClientTransferPlatformReportVo
import com.onepiece.gpgaming.beans.value.database.ClientTransferReportVo
import com.onepiece.gpgaming.beans.value.database.MemberTransferPlatformReportVo
import com.onepiece.gpgaming.beans.value.database.MemberTransferReportVo
import com.onepiece.gpgaming.beans.value.database.TransferOrderCo
import com.onepiece.gpgaming.beans.value.database.TransferOrderReportVo
import com.onepiece.gpgaming.beans.value.database.TransferOrderUo
import com.onepiece.gpgaming.beans.value.internet.web.TransferOrderValue
import com.onepiece.gpgaming.core.dao.TransferOrderDao
import com.onepiece.gpgaming.core.dao.TransferReportQuery
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class TransferOrderDaoImpl : BasicDaoImpl<TransferOrder>("transfer_order"), TransferOrderDao {

    override val mapper: (rs: ResultSet) -> TransferOrder
        get() = { rs ->
            val orderId = rs.getString("order_id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val username = rs.getString("username")
            val money = rs.getBigDecimal("money")
            val promotionAmount = rs.getBigDecimal("promotion_amount")
            val joinPromotionId = rs.getInt("join_promotion_id")
            val promotionJson = rs.getString("promotion_json")
            val from = rs.getString("from").let { Platform.valueOf(it) }
            val to = rs.getString("to").let { Platform.valueOf(it) }
            val state = rs.getString("state").let { TransferState.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val updatedTime = rs.getTimestamp("updated_time").toLocalDateTime()
            TransferOrder(orderId = orderId, clientId = clientId, memberId = memberId, money = money, promotionAmount = promotionAmount,
                    from = from, to = to, state = state, createdTime = createdTime, updatedTime = updatedTime, joinPromotionId = joinPromotionId,
                    promotionJson = promotionJson, username = username)
        }

    override fun create(transferOrderCo: TransferOrderCo): Boolean {
        return insert()
                .set("order_id", transferOrderCo.orderId)
                .set("client_id", transferOrderCo.clientId)
                .set("member_id", transferOrderCo.memberId)
                .set("username", transferOrderCo.username)
                .set("money", transferOrderCo.money)
                .set("promotion_amount", transferOrderCo.promotionAmount)
                .set("`from`", transferOrderCo.from)
                .set("`to`", transferOrderCo.to)
                .set("join_promotion_id", transferOrderCo.joinPromotionId)
                .set("promotion_json", transferOrderCo.promotionJson)
                .set("state", TransferState.Process)
                .executeOnlyOne()
    }

    override fun update(transferOrderUo: TransferOrderUo): Boolean {
        return update()
                .set("state", transferOrderUo.state)
                .where("order_id", transferOrderUo.orderId)
                .executeOnlyOne()
    }

    override fun memberPlatformReport(query: TransferReportQuery): List<MemberTransferPlatformReportVo> {
        return query("client_id, member_id, `from`, `to`, sum(money) as money")
                .asWhere("created_time >= ?", query.startDate)
                .asWhere("created_time < ?", query.endDate)
                .where("state", TransferState.Successful)
                .where("member_id", query.memberId)
                .group("client_id, member_id, `from`, `to`")
                .execute {  rs ->

                    val clientId = rs.getInt("client_id")
                    val memberId = rs.getInt("member_id")
                    val from = rs.getString("from").let { Platform.valueOf(it) }
                    val to = rs.getString("to").let{ Platform.valueOf(it) }
                    val money = rs.getBigDecimal("money")
                    MemberTransferPlatformReportVo(clientId = clientId, memberId = memberId, from = from, to = to, money = money)
                }
    }

    override fun memberReport(query: TransferReportQuery): List<MemberTransferReportVo> {
        return query("client_id, member_id, sum(money) as money, sum(promotion_amount) as promotion_amount")
                .asWhere("created_time >= ?", query.startDate)
                .asWhere("created_time < ?", query.endDate)
                .where("state", TransferState.Successful)
                .where("from", query.from)
                .where("to", query.to)
                .where("member_id", query.memberId)
                .group("client_id, member_id")
                .execute {  rs ->

                    val clientId = rs.getInt("client_id")
                    val memberId = rs.getInt("member_id")
                    val money = rs.getBigDecimal("money")
                    val promotionAmount = rs.getBigDecimal("promotion_amount")

                    MemberTransferReportVo(clientId = clientId, memberId = memberId, money = money, promotionAmount = promotionAmount)
                }

    }

    override fun clientPlatformReport(query: TransferReportQuery): List<ClientTransferPlatformReportVo> {
        return query("client_id, `from`, `to`, sum(money) as money, sum(promotion_amount) as promotion_amount")
                .asWhere("created_time >= ?", query.startDate)
                .asWhere("created_time < ?", query.endDate)
                .where("state", TransferState.Successful)
                .where("client_id", query.clientId)
                .group("client_id, `from`, `to`")
                .execute {  rs ->

                    val clientId = rs.getInt("client_id")
                    val from = rs.getString("from").let { Platform.valueOf(it) }
                    val to = rs.getString("to").let{ Platform.valueOf(it) }
                    val money = rs.getBigDecimal("money")
                    val promotionAmount = rs.getBigDecimal("promotion_amount")

                    ClientTransferPlatformReportVo(clientId = clientId, from = from, to = to, money = money, promotionAmount = promotionAmount)
                }

    }

    override fun clientReport(query: TransferReportQuery): List<ClientTransferReportVo> {

        return query("client_id, member_id, sum(money) as money")
                .asWhere("created_time >= ?", query.startDate)
                .asWhere("created_time < ?", query.endDate)
                .where("state", TransferState.Successful)
                .where("from", query.from)
                .where("to", query.to)
                .where("client_id", query.clientId)
                .group("client_id")
                .execute {  rs ->

                    val clientId = rs.getInt("client_id")
                    val money = rs.getBigDecimal("money")


                    val transferIn: BigDecimal
                    val transferOut: BigDecimal

                    if (query.from == Platform.Center) {
                        transferIn = money
                        transferOut = BigDecimal.ZERO
                    } else {
                        transferIn = BigDecimal.ZERO
                        transferOut = money
                    }

                    ClientTransferReportVo(clientId = clientId, transferIn = transferIn, transferOut = transferOut)
                }
    }

    override fun query(query: TransferOrderValue.Query): List<TransferOrder> {
        return query()
                .where("client_id", query.clientId)
                .where("from", query.from)
                .where("join_promotion_id", query.promotionId)
                .sort("created_time desc")
                .execute(mapper)

    }

    override fun report(startDate: LocalDate): List<TransferOrderReportVo> {
        return query("client_id, `to`, join_promotion_Id, sum(promotion_amount) as promotion_amount")
                .asWhere("join_promotion_id is not null")
                .asWhere("created_time >= ?", startDate)
                .asWhere("created_time < ?", startDate.plusDays(1))
                .group("client_id, `to`, join_promotion_Id")
                .execute { rs ->
                    val clientId = rs.getInt("client_id")
                    val platform = rs.getString("to").let { Platform.valueOf(it) }
                    val promotionId = rs.getInt("join_promotion_Id")
                    val promotionAmount = rs.getBigDecimal("promotion_amount")
                    TransferOrderReportVo(clientId = clientId, platform = platform, promotionId = promotionId, promotionAmount = promotionAmount)
                }
    }
}