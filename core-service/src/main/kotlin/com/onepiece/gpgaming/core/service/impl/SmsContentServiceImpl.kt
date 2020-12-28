package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.SmsContent
import com.onepiece.gpgaming.beans.value.database.SmsContentValue
import com.onepiece.gpgaming.core.dao.SmsContentDao
import com.onepiece.gpgaming.core.service.SmsContentService
import org.springframework.stereotype.Service

@Service
class SmsContentServiceImpl(
        private val smsContentDao: SmsContentDao
) : SmsContentService {

    override fun create(co: SmsContentValue.SmsContentCo): Int {
        val id = smsContentDao.create(smsContentCo = co)
        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }
        return id
    }

    override fun findLastSms(memberId: Int): SmsContent? {
        return smsContentDao.findLastSms(memberId = memberId)
    }

    override fun findLastSms(phone: String): SmsContent? {
        return smsContentDao.findLastSms(phone = phone)
    }
}