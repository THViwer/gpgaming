package com.onepiece.gpgaming.player.sms

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.value.database.SmsContentValue
import com.onepiece.gpgaming.core.service.SmsContentService
import com.onepiece.gpgaming.games.http.OkHttpUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class SmsService(
        private val okHttpUtil: OkHttpUtil,
        private val smsContentService: SmsContentService
) {

    private val log = LoggerFactory.getLogger(SmsService::class.java)

    companion object {
        const val path = "https://www.sms123.net/api/send.php"
        const val apiKey = "105155917afc6231e03e5240a54d3121"
    }


    data class SmsResponse(

            val status: String,

            @JsonIgnore
            @JsonAnySetter
            val resultData: Map<String, Any> = hashMapOf()
    )


    fun send(clientId: Int, mobile: String, message: String) {

        val successful = try {
            val url = "$path?apiKey=$apiKey&messageContent=$message&recipients=$mobile&referenceID=${UUID.randomUUID().toString().replace("=", "")}"
            val response = okHttpUtil.doGet(platform = Platform.Center, url = url, clz = SmsResponse::class.java)
            log.info("send sms message response: $response")
//
            response.status == "ok"
        } catch (e: Exception) {
            log.error("send message error", e)
            false
        }

        val co = SmsContentValue.SmsContentCo(clientId = clientId, levelId = null, memberIds = null, phones = mobile, content = message, successful = successful)
        smsContentService.create(co = co)
    }


}

//fun main() {
//    val okHttpUtil = OkHttpUtil(objectMapper = jacksonObjectMapper(), xmlMapper = XmlMapper())
//    val smsService = SmsService(okHttpUtil)
//    smsService.send(clientId = 1, mobile = "60134238622", message = "hi, to11m11111")
//}