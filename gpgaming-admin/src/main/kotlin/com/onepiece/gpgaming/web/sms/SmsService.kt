package com.onepiece.gpgaming.web.sms

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
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
        const val MAX_SPLIT = 1
        const val path = "https://www.sms123.net/api/send.php"
        const val apiKey = "105155917afc6231e03e5240a54d3121"
    }


    data class SmsResponse(

            val status: String,

            @JsonIgnore
            @JsonAnySetter
            val resultData: Map<String, Any> = hashMapOf()
    )


    fun send(clientId: Int, message: String, mobiles: List<String>) {

        repeat(mobiles.size) { x ->
            val start = x * MAX_SPLIT
            val end = ((x + 1) * MAX_SPLIT).let {
                if (mobiles.size < it) mobiles.size else it
            }

            val data = mobiles.subList(start, end)


            val successful = try {
                val url = "$path?apiKey=$apiKey&messageContent=$message&recipients=${data.joinToString(";")}&referenceID=${UUID.randomUUID().toString().replace("=", "")}"
                val response = okHttpUtil.doGet(platform = Platform.Center, url = url, clz = SmsResponse::class.java)
                log.info("send sms message response: $response")
//
                response.status == "ok"
            } catch (e: Exception) {
                log.error("send message error", e)
                false
            }

            val co = SmsContentValue.SmsContentCo(clientId = clientId, levelId = null, memberIds = null, phones = data.joinToString(separator = ","), content = message, successful = successful)
            smsContentService.create(co = co)

            if (mobiles.size <= end) return
        }
    }


    fun send(clientId: Int, mobile: String, message: String) {
        this.send(clientId = clientId, mobiles = listOf(mobile), message = message)
    }


}

//fun main() {
//    val okHttpUtil = OkHttpUtil(objectMapper = jacksonObjectMapper(), xmlMapper = XmlMapper())
//    val smsService = SmsService(okHttpUtil)
//    smsService.send(clientId = 1, mobiles = listOf("60134238622", "60174938499"), message = "hi, bbbbbbb ")
//}