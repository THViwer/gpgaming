package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.ShowPosition
import com.onepiece.gpgaming.beans.model.ClientConfig
import com.onepiece.gpgaming.beans.value.internet.web.ClientConfigValue
import com.onepiece.gpgaming.core.dao.ClientConfigDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet

@Repository
class ClientConfigDaoImpl : BasicDaoImpl<ClientConfig>("client_config"), ClientConfigDao {

    override val mapper: (rs: ResultSet) -> ClientConfig
        get() = { rs ->

            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val title = rs.getString("title")
            val keywords = rs.getString("keywords")
            val description = rs.getString("description")
            val liveChatId = rs.getString("live_chat_id")
            val liveChatTab = rs.getBoolean("live_chat_tab")
            val googleStatisticsId = rs.getString("google_statistics_id")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val facebookTr = rs.getString("facebook_tr")
            val facebookShowPosition = rs.getString("facebook_show_position")
                    .let { ShowPosition.valueOf(it) }
            val asgContent = rs.getString("asg_content")
            val enableRegisterMessage = rs.getBoolean("enable_register_message")
            val registerMessageTemplate = rs.getString("register_message_template")

            val enableIntroduce = rs.getBoolean("enable_introduce")
            val introducePromotionId = rs.getInt("introduce_promotion_id")
            val registerCommission = rs.getBigDecimal("register_commission")
            val depositPeriod = rs.getBigDecimal("deposit_period")
            val commissionPeriod = rs.getInt("commission_period")
            val depositCommission = rs.getBigDecimal("deposit_commission")
            val shareTemplate = rs.getString("share_template")


            ClientConfig(id = id, clientId = clientId, keywords = keywords, description = description, createdTime = createdTime,
                    title = title, liveChatId = liveChatId, googleStatisticsId = googleStatisticsId, facebookTr = facebookTr,
                    liveChatTab = liveChatTab, asgContent = asgContent, facebookShowPosition = facebookShowPosition,
                    enableRegisterMessage = enableRegisterMessage, registerMessageTemplate = registerMessageTemplate,
                    enableIntroduce = enableIntroduce, introducePromotionId = introducePromotionId, registerCommission = registerCommission,
                    depositPeriod = depositPeriod, commissionPeriod = commissionPeriod, depositCommission = depositCommission,
                    shareTemplate = shareTemplate)
        }

    override fun create(configUo: ClientConfigValue.ClientConfigUo): Boolean {
        return insert()
                .set("client_id", configUo.clientId)
                .set("title", configUo.title)
                .set("keywords", configUo.keywords)
                .set("description", configUo.description)
                .set("live_chat_id", configUo.liveChatId)
                .set("live_chat_tab", configUo.liveChatTab)
                .set("google_statistics_id", configUo.googleStatisticsId)
                .set("facebook_tr", configUo.facebookTr)
                .set("facebook_show_position", configUo.facebookShowPosition)

                .set("enable_introduce", false)
                .set("introduce_promotion_id", -1)
                .set("register_commission", BigDecimal.ZERO)
                .set("deposit_period", 0)
                .set("commission_period", 0)
                .set("deposit_commission", 0)
                .set("share_template", "")

                .executeOnlyOne()
    }

    override fun update(configUo: ClientConfigValue.ClientConfigUo): Boolean {
        return update()
                .set("title", configUo.title)
                .set("keywords", configUo.keywords)
                .set("description", configUo.description)
                .set("live_chat_id", configUo.liveChatId)
                .set("live_chat_tab", configUo.liveChatTab)
                .set("google_statistics_id", configUo.googleStatisticsId)
                .set("facebook_tr", configUo.facebookTr)
                .set("facebook_show_position", configUo.facebookShowPosition)
                .set("asg_content", configUo.asgContent)
                .where("client_id", configUo.clientId)
                .executeOnlyOne()
    }

    override fun update(id: Int, enableRegisterMessage: Boolean, registerMessageTemplate: String): Boolean {
        return update()
                .set("enable_register_message", enableRegisterMessage)
                .set("register_message_template", registerMessageTemplate)
                .where("id", id)
                .executeOnlyOne()
    }

    override fun update(uo: ClientConfigValue.IntroduceUo): Boolean {
        return update()
                .set("enable_introduce", uo.enableIntroduce)
                .set("introduce_promotion_id", uo.introducePromotionId)
                .set("register_commission", uo.registerCommission)
                .set("deposit_period", uo.depositPeriod)
                .set("commission_period", uo.commissionPeriod)
                .set("deposit_commission", uo.depositCommission)
                .set("share_template", uo.shareTemplate)
                .where("client_id", uo.clientId)
                .executeOnlyOne()
    }
}