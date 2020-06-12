package com.onepiece.gpgaming.payment

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.pay.GPPayConfig
import com.onepiece.gpgaming.payment.http.PayOkHttpUtil
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal


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

        // 商户端订单Id
        val orderId: String,

        // 金额
        val amount: BigDecimal,

        // 银行
        val bank: Bank,

        // 商户回调地址
        val merchantBackPath: String = "http://localhost:8011/api/v1/admin/demo/pay"
)

@Service
class FpxService(
        private val okHttpUtil: PayOkHttpUtil
) : PayService {

    override fun start(req: PayRequest): Map<String, Any> {
        val config = req.payConfig as GPPayConfig

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
                bank = req.selectBank!!, merchantBackPath = config.backendURL)

        val response = okHttpUtil.doPostJson(url = config.apiPath, data = fpxReq, clz = FPXPayResponse::class.java)
        check(response.errorCode != 200) { OnePieceExceptionCode.SYSTEM }


        return mapOf(
                "errorCode" to response.errorCode,
                "orderId" to response.orderId,
                "payUrl" to response.payUrl
        )
    }
}