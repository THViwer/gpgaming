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

    override fun create(co: SmsContentValue.SmsContentCo) {
        val flag = smsContentDao.create(smsContentCo = co)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun findLastSms(memberId: Int): SmsContent? {
        return smsContentDao.findLastSms(memberId = memberId)
    }
}