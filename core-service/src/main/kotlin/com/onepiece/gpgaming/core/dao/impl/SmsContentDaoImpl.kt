package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.SmsContent
import com.onepiece.gpgaming.beans.value.database.SmsContentValue
import com.onepiece.gpgaming.core.dao.SmsContentDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class SmsContentDaoImpl : SmsContentDao, BasicDaoImpl<SmsContent>("sms_content") {

    override val mapper: (rs: ResultSet) -> SmsContent
        get() = { rs ->
            val id = rs.getInt("id")
            val levelId = rs.getInt("level_id")
            val memberIds = rs.getString("member_ids")
            val phones = rs.getString("phones")
            val content = rs.getString("content")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            SmsContent(id = id, levelId = levelId, memberIds = memberIds, phones = phones, content = content,
                    createdTime = createdTime)
        }

    override fun create(smsContentCo: SmsContentValue.SmsContentCo): Boolean {
        return insert()
                .set("level_id", smsContentCo.levelId)
                .set("member_ids", smsContentCo.memberIds)
                .set("phones", smsContentCo.phones)
                .set("content", smsContentCo.content)
                .executeOnlyOne()
    }


}