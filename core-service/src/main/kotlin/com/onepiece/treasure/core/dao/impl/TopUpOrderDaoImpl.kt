package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.TopUpOrderDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.core.dao.value.TopUpOrderCo
import com.onepiece.treasure.core.dao.value.TopUpOrderQuery
import com.onepiece.treasure.core.dao.value.TopUpOrderUo
import com.onepiece.treasure.core.model.TopUpOrder
import com.onepiece.treasure.core.model.enums.Banks
import com.onepiece.treasure.core.model.enums.TopUpState
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

@Repository
class TopUpOrderDaoImpl : BasicDaoImpl<TopUpOrder>("topup_order"), TopUpOrderDao {

    override fun mapper(): (rs: ResultSet) -> TopUpOrder {
        return { rs ->
            val id = rs.getInt("id")
            val orderId = rs.getString("order_id")
            val processId = rs.getString("process_id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val bank = rs.getString("bank").let { Banks.valueOf(it) }
            val bankCardNumber = rs.getString("bank_card_number")
            val money = rs.getBigDecimal("money")
            val imgPath = rs.getString("imgPath")
            val state = rs.getString("state").let { TopUpState.valueOf(it) }
            val remarks = rs.getString("remarks")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val endTime = rs.getTimestamp("end_time")?.toLocalDateTime()
            TopUpOrder(id = id, orderId = orderId, clientId = clientId, memberId = memberId, bank = bank, money = money,
                    imgPath = imgPath, state = state, remarks = remarks, createdTime = createdTime, endTime = endTime,
                    bankCardNumber = bankCardNumber, processId = processId)
        }
    }

    override fun query(query: TopUpOrderQuery): List<TopUpOrder> {
        return query().where("client_id", query.clientId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .where("member_id", query.memberId)
                .where("order_id", query.orderId)
                .where("state", query.state)
                .execute(mapper())
    }

    override fun create(orderCo: TopUpOrderCo): Boolean {
        return insert().set("order_id", orderCo.orderId)
                .set("process_id", UUID.randomUUID().toString())
                .set("client_id", orderCo.clientId)
                .set("member_id", orderCo.memberId)
                .set("bank", orderCo.bank)
                .set("bank_card_number", orderCo.bankCardNumber)
                .set("money", orderCo.money)
                .set("imgPath", orderCo.imgPath)
                .set("state", TopUpState.Process)
                .executeOnlyOne()
    }

    override fun update(orderUo: TopUpOrderUo): Boolean {
        val sql = "update topup_order set state = ?, remarks = ?, process_id = ? where order_id = ? and process_id = ?"
        return jdbcTemplate.update(sql, orderUo.state.name, orderUo.remarks, UUID.randomUUID().toString(), orderUo.orderId, orderUo.processId) == 1
    }
}