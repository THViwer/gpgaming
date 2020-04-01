package com.onepiece.gpgaming.payment

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.model.pay.M3PayConfig
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.util.*

@Service
class M3PayService(
        private val objectMapper: ObjectMapper
): PayService {

    override fun start(req: PayRequest): Map<String, Any> {

        val config = req.payConfig as M3PayConfig

        val data = listOf(
                config.merchantKey,
                config.memberCode,
                req.orderId,
                req.amount.setScale(2, 2).toString().replace(".", "")
        )
        val signStr = data.joinToString(separator = "")
        val sign = DigestUtils.sha256Hex(signStr)

        return mapOf(
                "MerchantCode" to config.memberCode,
                "RefNo" to req.orderId,
                "Amount" to req.amount.setScale(2, 2),
                "UserName" to req.username,
                "UserContact" to req.username,
                "UserEmail" to "${req.username}@gmail.com",
                "Signature" to sign,
                "ResponseURL" to req.responseUrl,
                "BackendURL" to config.backendURL
        )

//        val body = FormBody.Builder()
//        body.add("MerchantCode", config.memberCode)
//        body.add("RefNo", orderId)
//        body.add("Amount", "10.00")
//        body.add("Username", "cabb")
//        body.add("UserEmail", "cabb@gmail.com")
//        body.add("UserContact", "6012431564")
//        body.add("Signature", sign)
//        body.add("ResponseURL", "http://www.google.com")
//        body.add("BackendURL", "http://www.google.com")
//        val response = okHttpUtil.doPostForm(pay = PayType.M3Pay, url = "https://payment.m3pay.com/epayment/entry.aspx",
//                body = body.build(), clz = M3PayValue.M3PayResponse::class.java)
    }



}

fun main() {
    val orderId = UUID.randomUUID().toString().replace("-", "").substring(0, 10)
    val data = listOf(
            "dbb06d88-38ae-4aa5-a49a-2f77ce31efd8",
            "T004",
            orderId,
            "1000"
    )
    val signStr = data.joinToString(separator = "")
    val sign = DigestUtils.sha256Hex(signStr)

    println("orderId: $orderId")
    println("sign: $sign")
}