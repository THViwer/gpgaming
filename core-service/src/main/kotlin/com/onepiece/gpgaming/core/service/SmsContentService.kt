package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.SmsContent
import com.onepiece.gpgaming.beans.value.database.SmsContentValue

interface SmsContentService {

    fun create(co: SmsContentValue.SmsContentCo): Int

    fun findLastSms(memberId: Int): SmsContent?

    fun findLastSms(phone: String): SmsContent?

}