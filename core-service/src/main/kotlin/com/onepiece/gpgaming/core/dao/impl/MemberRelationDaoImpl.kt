package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.MemberRelation
import com.onepiece.gpgaming.beans.value.database.MemberRelationValue
import com.onepiece.gpgaming.core.dao.MemberRelationDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import com.onepiece.gpgaming.core.dao.basic.getIntOrNull
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class MemberRelationDaoImpl : BasicDaoImpl<MemberRelation>("member_relation"), MemberRelationDao {

    override val mapper: (rs: ResultSet) -> MemberRelation
        get() = { rs ->

            val id = rs.getInt("id")
            val bossId = rs.getInt("boss_id")
            val memberId = rs.getInt("member_id")
            val r1 = rs.getInt("r1")
            val r2 = rs.getIntOrNull("r2")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            MemberRelation(id = id, bossId = bossId, memberId = memberId, r1 = r1, r2 = r2, createdTime = createdTime)
        }

    override fun create(co: MemberRelationValue.MemberRelationCo): Boolean {
        return insert()
                .set("boss_id", co.bossId)
                .set("member_id", co.memberId)
                .set("r1", co.r1)
                .set("r2", co.r2)
                .executeOnlyOne()
    }
}