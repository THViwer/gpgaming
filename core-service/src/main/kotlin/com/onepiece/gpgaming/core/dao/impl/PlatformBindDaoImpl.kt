package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.PlatformBind
import com.onepiece.gpgaming.beans.value.database.PlatformBindCo
import com.onepiece.gpgaming.beans.value.database.PlatformBindUo
import com.onepiece.gpgaming.core.dao.PlatformBindDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet
import java.util.*

@Repository
class PlatformBindDaoImpl: BasicDaoImpl<PlatformBind>("platform_bind"), PlatformBindDao {

    override val mapper: (rs: ResultSet) -> PlatformBind
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val username = rs.getString("username")
            val password = rs.getString("password")
            val earnestBalance = rs.getBigDecimal("earnest_balance")
            val processId = rs.getString("process_id")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val hot = rs.getBoolean("hot")
            val new = rs.getBoolean("new")


            val name = rs.getString("name")
            val icon = rs.getString("icon")
            val mobileIcon = rs.getString("mobile_icon")
            val disableIcon = rs.getString("disable_icon")
            val mobileDisableIcon = rs.getString("mobile_disable_icon")
            val originIcon = rs.getString("origin_icon")
            val originIconOver = rs.getString("origin_icon_over")
            val platformDetailIcon = rs.getString("platform_detail_icon")
            val platformDetailIconOver = rs.getString("platform_detail_icon_over")


            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            val tokenJson = rs.getString("token_json")
            PlatformBind(id = id, clientId = clientId, platform = platform, status = status, createdTime = createdTime,
                    username = username, password = password, processId = processId, earnestBalance = earnestBalance,
                    tokenJson = tokenJson, hot = hot, new = new, name = name, icon = icon, mobileIcon = mobileIcon,
                    disableIcon = disableIcon, mobileDisableIcon = mobileDisableIcon, originIcon = originIcon, originIconOver = originIconOver,
                    platformDetailIcon = platformDetailIcon, platformDetailIconOver = platformDetailIconOver)
        }

    override fun get(id: Int): PlatformBind {
        val sql = "select * from platform_bind where id = ?"
        return jdbcTemplate.query(sql, arrayOf<Any>(id)) { rs, _ ->
            mapper(rs)
        }.first()
    }
    override fun allWithDel(clientId: Int): List<PlatformBind> {
        val sql = "select * from platform_bind where client_id = ?"
        return jdbcTemplate.query(sql, arrayOf<Any>(clientId)) { rs, _ ->
            mapper(rs)
        }
    }

    override fun find(platform: Platform): List<PlatformBind> {
        return query().where("platform", platform).execute(mapper)
    }

    override fun create(platformBindCo: PlatformBindCo): Boolean {
        return insert().set("client_id", platformBindCo.clientId)
                .set("platform", platformBindCo.platform)
                .set("username", platformBindCo.username)
                .set("password", platformBindCo.password)
                .set("token_json", platformBindCo.tokenJson)
                .set("earnest_balance", platformBindCo.earnestBalance)
                .set("hot", false)
                .set("new", false)
                .set("process_id", UUID.randomUUID().toString())

                .set("name", platformBindCo.name)
                .set("icon", platformBindCo.icon)
                .set("mobile_icon", platformBindCo.mobileIcon)
                .set("disable_icon", platformBindCo.disableIcon)
                .set("mobile_disable_icon", platformBindCo.mobileDisableIcon)
                .set("origin_icon", platformBindCo.originIcon)
                .set("origin_icon_over", platformBindCo.originIconOver)
                .set("platform_detail_icon", platformBindCo.platformDetailIcon)
                .set("platform_detail_icon_over", platformBindCo.platformDetailIconOver)

                .executeOnlyOne()
    }

    override fun update(platformBindUo: PlatformBindUo): Boolean {
        return update().set("status", platformBindUo.status)
                .set("username", platformBindUo.username)
                .set("password", platformBindUo.password)
                .set("token_json", platformBindUo.tokenJson)
                .set("earnest_balance", platformBindUo.earnestBalance)
                .set("hot", platformBindUo.hot)
                .set("new", platformBindUo.new)

                .set("name", platformBindUo.name)
                .set("icon", platformBindUo.icon)
                .set("mobile_icon", platformBindUo.mobileIcon)
                .set("disable_icon", platformBindUo.disableIcon)
                .set("mobile_disable_icon", platformBindUo.mobileDisableIcon)
                .set("origin_icon", platformBindUo.originIcon)
                .set("origin_icon_over", platformBindUo.originIconOver)
                .set("platform_detail_icon", platformBindUo.platformDetailIcon)
                .set("platform_detail_icon_over", platformBindUo.platformDetailIconOver)

                .where("id", platformBindUo.id)
                .executeOnlyOne()
    }

    override fun find(clientId: Int, platform: Platform): PlatformBind {
        return query()
                .where("client_id", clientId)
                .where("platform", platform)
                .executeOnlyOne(mapper)

    }

    override fun updateEarnestBalance(id: Int, earnestBalance: BigDecimal, processId: String): Boolean {
        val builder = update()
        if (earnestBalance.toDouble() > 0) {
            builder.asSet("earnest_balance = earnest_balance + $earnestBalance")
        } else {
            builder.asSet("earnest_balance = earnest_balance - ${earnestBalance.abs()}")
        }
//        builder.set("process_id", UUID.randomUUID().toString())
                .where("id", id)
//                .where("process_id", processId)

        if (earnestBalance.toDouble() < 0) {
            builder.asWhere("earnest_balance >= ?", earnestBalance.abs())
        }
        return builder.executeOnlyOne()
    }

}