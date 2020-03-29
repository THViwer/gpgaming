package com.onepiece.gpgaming.payment

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onepiece.gpgaming.beans.enums.PayType
import com.onepiece.gpgaming.beans.model.pay.M3PayConfig
import com.onepiece.gpgaming.beans.model.pay.PayConfig
import com.onepiece.gpgaming.payment.http.OkHttpUtil
import okhttp3.FormBody
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class M3PayService(
        private val okHttpUtil: OkHttpUtil
)  {

    fun start(payConfig: PayConfig) {

        val amount = BigDecimal.valueOf(10)
        val orderId = UUID.randomUUID().toString().replace("-", "").substring(0, 10)

        val config = payConfig as M3PayConfig

        val body = FormBody.Builder()
        body.add("MerchantCode", config.memberCode)
        body.add("RefNo", orderId)
        body.add("Amount", "10.00")

        // (MerchantKey & MerchantCode & RefNo & Amount)
        val data = listOf(
                config.merchantKey,
                config.memberCode,
                orderId,
                amount.setScale(2, 2).toString().replace(".", "")
        )
        val signStr = data.joinToString(separator = "")
        println("签名串：$signStr")
        val sign = DigestUtils.sha256Hex(signStr)

        body.add("Username", "cabb")
        body.add("UserEmail", "cabb@gmail.com")
        body.add("UserContact", "6012431564")
        body.add("Signature", sign)
        body.add("ResponseURL", "http://www.google.com")
        body.add("BackendURL", "http://www.google.com")

        println("success")
        val response = okHttpUtil.doPostForm(pay = PayType.M3Pay, url = "https://payment.m3pay.com/epayment/entry.aspx",
                body = body.build(), clz = M3PayValue.M3PayResponse::class.java)
        println("$response")
    }



}

fun main() {
//    val v = "orangeM01TRANS0000112001"
//    val sign = DigestUtils.sha256Hex(v)
//    println(sign)

    val mapper = jacksonObjectMapper()
    val xmlMapper = XmlMapper()
    val okHttpUtil = OkHttpUtil(mapper, xmlMapper)

    val m3PayService = M3PayService(okHttpUtil)
    val payConfig = M3PayConfig()
    m3PayService.start(payConfig)

}