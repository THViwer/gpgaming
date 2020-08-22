package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.MemberIntroduce
import com.onepiece.gpgaming.beans.value.database.MemberIntroduceValue
import com.onepiece.gpgaming.core.dao.MemberIntroduceDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet

@Repository
class MemberIntroduceDaoImpl : BasicDaoImpl<MemberIntroduce>("member_introduce"), MemberIntroduceDao {

    override val mapper: (rs: ResultSet) -> MemberIntroduce
        get() = { rs ->

            val id = rs.getInt("id")
            val memberId = rs.getInt("member_id")
            val introduceId = rs.getInt("introduce_id")
            val registerActivity = rs.getBoolean("register_activity")
            val depositActivity = rs.getBoolean("deposit_activity")

            val introduceCommission = rs.getBigDecimal("introduce_commission")

            val registerIp = rs.getString("register_ip")
            val name = rs.getString("name")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            MemberIntroduce(id = id, memberId = memberId, introduceId = introduceId, registerActivity = registerActivity, depositActivity = depositActivity,
                    introduceCommission = introduceCommission, registerIp = registerIp, name = name, createdTime = createdTime)

        }

    override fun create(co: MemberIntroduceValue.MemberIntroduceCo): Boolean {
        return insert()
                .set("member_id", co.memberId)
                .set("introduce_id", co.introduceId)
                .set("register_activity", false)
                .set("deposit_activity", false)
                .set("introduce_commission", BigDecimal.ZERO)
                .set("register_ip", co.registerIp)
                .set("name", co.name)
                .executeOnlyOne()
    }

    override fun update(uo: MemberIntroduceValue.MemberIntroduceUo): Boolean {
        return update()
                .set("register_activity", uo.registerActivity)
                .set("deposit_activity", uo.depositActivity)
                .set("introduce_commission", uo.introduceCommission)
                .where("id", uo.id)
                .executeOnlyOne()

    }

    override fun getByMemberId(memberId: Int): MemberIntroduce? {
        return query()
                .where("member_id", memberId)
                .executeMaybeOne(mapper)
    }

    override fun list(query: MemberIntroduceValue.MemberIntroduceQuery): List<MemberIntroduce> {
        return query()
                .where("introduce_id", query.introduceId)
                .execute(mapper)
    }
}