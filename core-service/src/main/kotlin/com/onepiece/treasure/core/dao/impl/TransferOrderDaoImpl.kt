package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.TransferState
import com.onepiece.treasure.beans.model.TransferOrder
import com.onepiece.treasure.beans.value.database.*
import com.onepiece.treasure.core.dao.TransferOrderDao
import com.onepiece.treasure.core.dao.TransferReportQuery
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import reactor.core.publisher.toMono
import java.math.BigDecimal
import java.sql.ResultSet

@Repository
class TransferOrderDaoImpl : BasicDaoImpl<TransferOrder>("transfer_order"), TransferOrderDao {

    override val mapper: (rs: ResultSet) -> TransferOrder
        get() = { rs ->
            val orderId = rs.getString("order_id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val money = rs.getBigDecimal("money")
            val giftMoney = rs.getBigDecimal("gift_money")
            val from = rs.getString("from").let { Platform.valueOf(it) }
            val to = rs.getString("to").let { Platform.valueOf(it) }
            val state = rs.getString("state").let { TransferState.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val updatedTime = rs.getTimestamp("updated_time").toLocalDateTime()
            TransferOrder(orderId = orderId, clientId = clientId, memberId = memberId, money = money, giftMoney = giftMoney,
                    from = from, to = to, state = state, createdTime = createdTime, updatedTime = updatedTime)
        }

    override fun create(transferOrderCo: TransferOrderCo): Boolean {
        return insert()
                .set("order_id", transferOrderCo.orderId)
                .set("client_id", transferOrderCo.clientId)
                .set("member_id", transferOrderCo.memberId)
                .set("money", transferOrderCo.money)
                .set("gift_money", transferOrderCo.giftMoney)
                .set("`from`", transferOrderCo.from)
                .set("`to`", transferOrderCo.to)
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
        return query("client_id, member_id, sum(money) as money")
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

                    MemberTransferReportVo(clientId = clientId, memberId = memberId, money = money)
                }

    }

    override fun clientPlatformReport(query: TransferReportQuery): List<ClientTransferPlatformReportVo> {
        return query("client_id, `from`, `to`, sum(money) as money")
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

                    ClientTransferPlatformReportVo(clientId = clientId, from = from, to = to, money = money)
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

}