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
            val clientId = rs.getInt("client_id")
            val levelId = rs.getInt("level_id")
            val memberIds = rs.getString("member_ids")
            val phones = rs.getString("phones")
            val content = rs.getString("content")
            val code = rs.getString("code")
            val successful = rs.getBoolean("successful")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            SmsContent(id = id, levelId = levelId, memberIds = memberIds, phones = phones, content = content,
                    createdTime = createdTime, clientId = clientId, successful = successful, code = code)
        }

    override fun create(smsContentCo: SmsContentValue.SmsContentCo): Boolean {
        return insert()
                .set("client_id", smsContentCo.clientId)
                .set("level_id", smsContentCo.levelId)
                .set("member_ids", smsContentCo.memberIds)
                .set("phones", smsContentCo.phones)
                .set("code", smsContentCo.code)
                .set("content", smsContentCo.content)
                .set("successful", smsContentCo.successful)
                .executeOnlyOne()
    }


    override fun findLastSms(memberId: Int): SmsContent? {
        return query()
                .where("member_ids", memberId)
                .asWhere("code is not null")
                .sort("id desc")
                .limit(0, 1)
                .executeMaybeOne(mapper)
    }
}