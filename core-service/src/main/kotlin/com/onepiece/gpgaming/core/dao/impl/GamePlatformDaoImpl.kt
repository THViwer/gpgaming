package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.GamePlatform
import com.onepiece.gpgaming.beans.value.database.GamePlatformValue
import com.onepiece.gpgaming.core.dao.GamePlatformDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class GamePlatformDaoImpl : BasicDaoImpl<GamePlatform>("game_platform"), GamePlatformDao {

    override val mapper: (rs: ResultSet) -> GamePlatform
        get() = { rs ->
            val id = rs.getInt("id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
//            val name = rs.getString("name")
//            val icon = rs.getString("icon")
//            val mobileIcon = rs.getString("mobile_icon")
//            val disableIcon = rs.getString("disable_icon")
//            val mobileDisableIcon = rs.getString("mobile_disable_icon")
//            val originIcon = rs.getString("origin_icon")
//            val originIconOver = rs.getString("origin_icon_over")
            val demo = rs.getBoolean("demo")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val launchs = rs.getString("launchs")
//            val platformDetailIcon = rs.getString("platform_detail_icon")
//            val platformDetailIconOver = rs.getString("platform_detail_icon_over")
            GamePlatform(id = id, platform = platform, demo = demo, status = status, launchs = launchs)
        }

    override fun create(co: GamePlatformValue.GamePlatformCo): Boolean {
        return insert()
                .set("platform", co.platform)
//                .set("name", co.name)
//                .set("icon", co.icon)
//                .set("mobile_icon", co.mobileIcon)
//                .set("disable_icon", co.disableIcon)
//                .set("mobile_disable_icon", co.mobileDisableIcon)
//                .set("origin_icon", co.originIcon)
//                .set("origin_icon_over", co.originIconOver)
                .set("demo", co.demo)
                .set("status", co.status)
                .set("launchs", co.launchs)
//                .set("platform_detail_icon", co.platformDetailIcon)
//                .set("platform_detail_icon_over", co.platformDetailIconOver)
                .executeOnlyOne()
    }

    override fun update(uo: GamePlatformValue.GamePlatformUo): Boolean {
        return update()
                .set("platform", uo.platform)
//                .set("name", uo.name)
//                .set("icon", uo.icon)
//                .set("mobile_icon", uo.mobileIcon)
//                .set("disable_icon", uo.disableIcon)
//                .set("mobile_disable_icon", uo.mobileDisableIcon)
//                .set("origin_icon", uo.originIcon)
//                .set("origin_icon_over", uo.originIconOver)
//                .set("platform_detail_icon", uo.platformDetailIcon)
//                .set("platform_detail_icon_over", uo.platformDetailIconOver)
                .set("demo", uo.demo)
                .set("status", uo.status)
                .set("launchs", uo.launchs)
                .where("id", uo.id)
                .executeOnlyOne()
    }
}