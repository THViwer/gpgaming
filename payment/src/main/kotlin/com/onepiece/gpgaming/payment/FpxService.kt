package com.onepiece.gpgaming.payment

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.pay.MaxiPayConfig
import com.onepiece.gpgaming.payment.http.PayOkHttpUtil
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*


data class FPXPayResponse(

        // 错误码
        val errorCode: Int = 0,

        // 订单Id
        val orderId: String,

        // 支付地址
        val payUrl: String
)

data class FPXPayRequest(

        // 商户Code
        val merchantCode: String,

        val nonce: String = "",

        val hash: String = "",

        // 商户端订单Id
        val orderId: String,

        // 金额
        val amount: BigDecimal,

        // 银行
        val bank: Bank,

        // 商户回调地址
        val merchantBackPath: String = "http://localhost:8011/api/v1/admin/demo/pay",

        // 支付成功跳转url
        val responseUrl: String,

        // 失败的跳转Url
        val failResponseUrl: String
)

@Service
class FpxService(
        private val okHttpUtil: PayOkHttpUtil
) : PayService {

    override fun start(req: PayRequest): Map<String, Any> {
        val config = req.payConfig as MaxiPayConfig

        //TODO 签名暂定
//        val signParam = ""
//        val token =  DigestUtils.md5Hex(signParam)

//        val param = mapOf<String, Any>(
//                "merchantCode" to config.merchantId,
//                "orderId" to req.orderId,
//                "amount" to req.amount.setScale(2, 2),
//                "bank" to req.selectBank!!,
//                "merchantBackPath" to config.backendURL
//        ).toMap()

        val fpxReq = FPXPayRequest(merchantCode = config.merchantId, orderId = req.orderId, amount = req.amount.setScale(2, 2),
                bank = req.selectBank!!, merchantBackPath = config.backendURL, responseUrl = req.responseUrl, failResponseUrl = req.failResponseUrl)
        val nonce = UUID.randomUUID().toString()

        val param = "${req.orderId}:${config.merchantId}:${config.apiKey}:${nonce}"
        val hash = DigestUtils.md5Hex(param)

        val response = okHttpUtil.doPostJson(url = config.apiPath, data = fpxReq.copy(nonce = nonce, hash = hash), clz = FPXPayResponse::class.java)
        check(response.errorCode != 200) { OnePieceExceptionCode.SYSTEM }


        return mapOf(
                "errorCode" to response.errorCode,
                "orderId" to response.orderId,
                "payUrl" to response.payUrl
        )
    }
}