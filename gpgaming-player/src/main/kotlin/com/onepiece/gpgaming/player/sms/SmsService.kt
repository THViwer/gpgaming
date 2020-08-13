package com.onepiece.gpgaming.player.sms

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.games.http.OkHttpUtil
import com.onepiece.gpgaming.utils.StringUtil
import org.springframework.stereotype.Service
import java.util.*

@Service
class SmsService(
        private val okHttpUtil: OkHttpUtil
) {

    companion object {
        const val path = "https://sgateway.onewaysms.com/apis10.aspx"
        const val apiusername =  "API99ZL5ZA1FA"
        const val apipassword = "API99ZL5ZA1FA99ZL5"
        const val languagetype = "1"
    }

    fun getCode(): String {
        return StringUtil.generateNumNonce(4)
    }

    fun getRegisterSmsMessage(code: String):  String {
        return  "Hi, this is your code: $code"
    }

    fun start(mobile:  String, message: String) {
        val senderid = UUID.randomUUID().toString().replace("-", "").substring(0, 11)
        val param  = listOf(
                "apiusername=$apiusername",
                "apipassword=$apipassword",
                "mobileno=${mobile}",
                "senderid=$senderid",
                "languagetype=$languagetype",
                "message=${message}"
        ).joinToString(separator = "&")
        val url =  "$path?$param"
        okHttpUtil.doGet(platform = Platform.Center, url = url,  clz = String::class.java)
    }


}