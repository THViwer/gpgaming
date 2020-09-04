package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.SmsContent
import com.onepiece.gpgaming.beans.value.database.SmsContentValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface SmsContentDao : BasicDao<SmsContent> {

    fun create(smsContentCo: SmsContentValue.SmsContentCo): Boolean

    fun findLastSms(memberId: Int): SmsContent?
}