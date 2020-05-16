package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Commission
import com.onepiece.gpgaming.beans.value.database.CommissionValue
import com.onepiece.gpgaming.core.dao.CommissionDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class CommissionDaoImpl: BasicDaoImpl<Commission>("commission"), CommissionDao {

    override val mapper: (rs: ResultSet) -> Commission
        get() = { rs ->

            val id = rs.getInt("id")
            val bossId = rs.getInt("boss_id")
            val activeCount = rs.getInt("active_count")
            val scale = rs.getBigDecimal("scale")
            val status = rs.getString("status").let { Status.valueOf(it) }
            Commission(id = id, bossId = bossId, activeCount = activeCount, scale = scale, status = status)
        }

    override fun list(bossId: Int): List<Commission> {
        return query()
                .where("boss_id", bossId)
                .execute(mapper)
    }

    override fun create(co: CommissionValue.CommissionCo): Boolean {
        return insert()
                .set("boss_id", co.bossId)
                .set("active_count", co.activeCount)
                .set("scale", co.scale)
                .set("status", co.status)
                .executeOnlyOne()
    }

    override fun update(uo: CommissionValue.CommissionUo): Boolean {
        return update()
                .set("active_count", uo.activeCount)
                .set("scale", uo.scale)
                .set("status", uo.status)
                .where("id", uo.id)
                .executeOnlyOne()
    }
}