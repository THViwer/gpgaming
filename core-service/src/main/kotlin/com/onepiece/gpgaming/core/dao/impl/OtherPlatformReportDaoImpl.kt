package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.OtherPlatformReport
import com.onepiece.gpgaming.beans.value.database.OtherPlatformReportValue
import com.onepiece.gpgaming.core.dao.OtherPlatformReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate


@Repository
class OtherPlatformReportDaoImpl : BasicDaoImpl<OtherPlatformReport>("other_platform_report"), OtherPlatformReportDao {

    override val mapper: (rs: ResultSet) -> OtherPlatformReport
        get() = { rs ->
            val id = rs.getInt("id")
//            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val day = rs.getDate("day").toLocalDate()
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val bet = rs.getBigDecimal("bet")
            val win = rs.getBigDecimal("win")
            val originData = rs.getString("origin_data")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            OtherPlatformReport(id = id, clientId = clientId, memberId = memberId, day = day,
                    platform = platform, bet = bet, win = win, originData = originData, createdTime = createdTime)
        }

    override fun list(startDate: LocalDate): List<OtherPlatformReport> {
        return query()
                .where("day", startDate)
                .execute(mapper)
    }

    override fun batch(data: List<OtherPlatformReportValue.PlatformReportCo>) {
        batchInsert(data = data)
//                .set("boss_id")
                .set("client_id")
                .set("member_id")
                .set("day")
                .set("platform")
                .set("bet")
                .set("win")
                .set("origin_data")
                .execute { ps, entity ->
                    var x = 0
//                    ps.setInt(++x, entity.bossId)
                    ps.setInt(++x, entity.clientId)
                    ps.setInt(++x, entity.memberId)
                    ps.setString(++x, "${entity.day}")
                    ps.setString(++x, "${entity.platform}")
                    ps.setBigDecimal(++x, entity.bet)
                    ps.setBigDecimal(++x, entity.win)
                    ps.setString(++x, entity.originData)
                }
    }

}