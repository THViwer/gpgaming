package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.value.database.SmsContentValue

interface SmsContentService {

    fun create(co: SmsContentValue.SmsContentCo)

}