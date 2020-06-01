package com.onepiece.gpgaming.payment

import com.onepiece.gpgaming.beans.model.pay.GPPayConfig
import com.onepiece.gpgaming.payment.http.PayOkHttpUtil
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service


data class GPPayResponse(

        // 错误码
        val errorCode: Int = 0,

        // 订单Id
        val orderId: String,

        // 支付地址
        val payUrl: String
)

@Service
class GPPayService(
        private val okHttpUtil: PayOkHttpUtil
) : PayService {

    override fun start(req: PayRequest): Map<String, Any> {
        val config = req.payConfig as GPPayConfig

        //TODO 签名暂定
        val signParam = ""
        val token =  DigestUtils.md5Hex(signParam)

        val param = mapOf<String, Any>(
                "merchantCode" to config.merchantId,
                "orderId" to req.orderId,
                "amount" to req.amount.setScale(2, 2),
                "bank" to req.selectBank!!,
                "merchantBackPath" to config.backendURL
        ).toMap()

        val response = okHttpUtil.doPostJson(url = config.apiPath, data = param, clz = GPPayResponse::class.java)


        return mapOf(
                "errorCode" to response.errorCode,
                "orderId" to response.orderId,
                "payUrl" to response.payUrl
        )
    }
}