package com.onepiece.gpgaming.player.sms

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.value.database.SmsContentValue
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.core.service.SmsContentService
import com.onepiece.gpgaming.games.http.OkHttpUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class SmsService(
    private val okHttpUtil: OkHttpUtil,
    private val smsContentService: SmsContentService,
    private val clientService: ClientService
) {

    private val log = LoggerFactory.getLogger(SmsService::class.java)

    companion object {
        const val MAX_SPLIT = 1
        const val path = "https://www.sms123.net/api/send.php"
        const val otherApiKey = "105155917afc6231e03e5240a54d3121"
        const val ujApiKey = "b0367dacb9ecf1b74a21e55dce145788"
        const val uupbetKey = "6dc30d2759f41253b0831b6ed195a7a2"
    }


    data class SmsResponse(

        val status: String,

        @JsonIgnore
        @JsonAnySetter
        val resultData: Map<String, Any> = hashMapOf()
    )


    fun send(clientId: Int, message: String, mobiles: List<String>, code: String? = null, memberIds: Int? = null): List<Int> {

        val ids = arrayListOf<Int>()
        repeat(mobiles.size) { x ->
            val start = x * MAX_SPLIT
            val end = ((x + 1) * MAX_SPLIT).let {
                if (mobiles.size < it) mobiles.size else it
            }

            val data = mobiles.subList(start, end)

            val apiKey = when (clientId) {
                2 -> uupbetKey
                10001, 10002 -> ujApiKey
                else -> otherApiKey
            }


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

            val co = SmsContentValue.SmsContentCo(
                clientId = clientId, levelId = null, memberIds = memberIds, phones = data.joinToString(separator = ","),
                content = message, successful = successful,
                code = code
            )
            val id = smsContentService.create(co = co)
            ids.add(id)

            if (mobiles.size <= end) return ids
        }
        return ids
    }


    fun send(clientId: Int, mobile: String, message: String): Int {

        // uupbet 和 uj 才能发短信
        when (clientId) {
            2,
            10001 -> { }
            else -> return 0
        }

        val client = clientService.get(id = clientId)
        val newMobile = when (client.country) {
            Country.Malaysia -> if (mobile.substring(0, 2) == "60") mobile else "60$mobile"
            Country.Singapore -> if (mobile.substring(0, 2) == "65") mobile else "65$mobile"
            Country.Indonesia -> if (mobile.substring(0, 2) == "62") mobile else "62$mobile"
            Country.Thailand -> if (mobile.substring(0, 2) == "66") mobile else "66$mobile"
            Country.Vietnam -> if (mobile.substring(0, 2) == "84") mobile else "84$mobile"
            else -> if (mobile.substring(0, 2) == "60") mobile else "60$mobile"
        }

        val ids = this.send(clientId = clientId, mobiles = listOf(newMobile), message = message)
        return ids.first()
    }

    fun send(clientId: Int, mobile: String, memberId: Int, message: String, code: String? = null): Int {

        // uupbet 和 uj 才能发短信
        when (clientId) {
            2,
            10001 -> { }
            else -> return 0
        }

        val client = clientService.get(id = clientId)
        val newMobile = when (client.country) {
            Country.Malaysia -> if (mobile.substring(0, 2) == "60") mobile else "60$mobile"
            Country.Singapore -> if (mobile.substring(0, 2) == "65") mobile else "65$mobile"
            Country.Indonesia -> if (mobile.substring(0, 2) == "62") mobile else "62$mobile"
            Country.Thailand -> if (mobile.substring(0, 2) == "66") mobile else "66$mobile"
            Country.Vietnam -> if (mobile.substring(0, 2) == "84") mobile else "84$mobile"
            else -> if (mobile.substring(0, 2) == "60") mobile else "60$mobile"
        }

        val ids = this.send(clientId = clientId, mobiles = listOf(newMobile), message = message, code = code, memberIds = memberId)
        return ids.first()
    }

}


//fun main() {
//    val okHttpUtil = OkHttpUtil(objectMapper = jacksonObjectMapper(), xmlMapper = XmlMapper())
//    val smsService = SmsService(okHttpUtil)
//    smsService.send(clientId = 1, mobiles = listOf("60134238622", "60174938499"), message = "hi, bbbbbbb ")
//}