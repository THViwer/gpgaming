package com.onepiece.gpgaming.player.sms

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.value.database.SmsContentValue
import com.onepiece.gpgaming.core.service.SmsContentService
import com.onepiece.gpgaming.games.http.OkHttpUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SmsService(
        private val okHttpUtil: OkHttpUtil,
        private val smsContentService: SmsContentService
) {

    private val log = LoggerFactory.getLogger(SmsService::class.java)

    companion object {
        const val path = "https://api.esms.com.my/sms/send"
        const val username = "tomcheng"
        const val password = "tomcheng0505"
    }

    data class SmsResponse(

            val id: String,

            val status: Int,

//            val creditDeducted: Int,

            val message: String,

//            val parts: Int,
//
//            val type: Int

            @JsonIgnore
            @JsonAnySetter
            val resultData: Map<String, Any> = hashMapOf()
    )


    fun send(mobile: String, message: String) {
        val param = """
            {
                "user": "$username",
                "pass": "$password",
                "to": "$mobile",
                "msg": "RM0.00 $message"
            }
        """.trimIndent()

        val response = okHttpUtil.doPostJson(platform = Platform.Center, url = path, data = param, clz = SmsResponse::class.java)
        log.info("send sms message response: $response")

        val co = SmsContentValue.SmsContentCo(levelId = null, memberIds = null, phones = mobile,  content = message)
        smsContentService.create(co =  co)

    }

//    companion object {
//        const val path = "https://sgateway.onewaysms.com/apis10.aspx"
//        const val apiusername =  "API99ZL5ZA1FA"
//        const val apipassword = "API99ZL5ZA1FA99ZL5"
//        const val languagetype = "1"
//    }
//
//    fun getCode(): String {
//        return StringUtil.generateNumNonce(4)
//    }
//
//    fun getRegisterSmsMessage(code: String):  String {
//        return  "Hi, this is your code: $code"
//    }
//
//    fun start(mobile:  String, message: String) {
//        val senderid = UUID.randomUUID().toString().replace("-", "").substring(0, 11)
//        val param  = listOf(
//                "apiusername=$apiusername",
//                "apipassword=$apipassword",
//                "mobileno=${mobile}",
//                "senderid=$senderid",
//                "languagetype=$languagetype",
//                "message=${message}"
//        ).joinToString(separator = "&")
//        val url =  "$path?$param"
//        okHttpUtil.doGet(platform = Platform.Center, url = url,  clz = String::class.java)
//
//        val co = SmsContentValue.SmsContentCo(levelId = null, memberIds = null, phones = mobile,  content = message)
//        smsContentService.create(co =  co)
//    }


}

//fun main() {
//
//    val okHttpUtil = OkHttpUtil(objectMapper = jacksonObjectMapper(), xmlMapper = XmlMapper())
//    val smsService = SmsService(okHttpUtil)
//    smsService.start(mobile = "60134238622", message = "hi, to11m")
//}