package com.onepiece.gpgaming.payment

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
        val orderId = UUID.randomUUID().toString()

        val config = payConfig as M3PayConfig

        val body = FormBody.Builder()
        body.add("MerchantCode", config.memberCode)
        body.add("RefNo", UUID.randomUUID().toString())
        body.add("Amount", "10.00")

        // (MerchantKey & MerchantCode & RefNo & Amount)
        val data = listOf(
                config.merchantKey,
                config.memberCode,
                orderId,
                amount.setScale(2, 2).toString().replace(".", "")
        )
        val sign = DigestUtils.sha256Hex(data.joinToString())

        body.add("Username", "cabb")
        body.add("UserEmail", "cabb@gmail.com")
        body.add("UserContact", "6012431564")
        body.add("Signature", sign)
        body.add("ResponseURL", "http://www.google.com")
        body.add("BackendURL", "http://www.google.com")

        val response = okHttpUtil.doPostForm(pay = PayType.M3Pay, url = "http://payment.m3pay.com/epayment/entry.aspx",
                body = body.build(), clz = M3PayValue.M3PayResponse::class.java)
        println("$response")
    }



}

fun main() {
    val v = "orangeM01TRANS0000112001"
    val sign = DigestUtils.sha256Hex(v)
    println(sign)
}