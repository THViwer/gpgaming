package com.onepiece.gpgaming.payment

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.model.pay.SurePayConfig
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service

@Service
class SurePayService: PayService {

    override fun start(req: PayRequest): Map<String, Any> {

        val config = req.payConfig as SurePayConfig

        val supportBank = config.supportBanks.first { it.bank  == req.selectBank }
        val amount = req.amount.setScale(2, 2)
        val customer  = "${req.clientId}_${req.memberId}"
        val lang = when (req.language) {
            Language.EN -> "EN"
            Language.MY -> "MY"
            Language.TH -> "TH"
            Language.ID ->  "ID"
            else -> "EN"
        }

        val signParam = "${config.merchantId}${amount}${req.orderId}${customer}${config.apiKey}${config.currency}${config.clientIp}"
        val token =  DigestUtils.md5Hex(signParam)

        return mapOf(
                "action" to "${config.apiPath}/fundtransfer/",
                "merchant" to config.merchantId,
                "amount" to "$amount",
                "refid"  to  req.orderId,
                "token" to token,
                "customer" to customer,
                "currency"  to config.currency,
                "Language" to lang,
                "bankcode" to supportBank.bankCode,
                "clientip" to config.clientIp,
                "post_url" to config.backendURL,
                "failed_return_url" to req.failResponseUrl,
                "return_url" to req.responseUrl
        )
    }
}
//
//fun main() {
//
//    val orderId  = UUID.randomUUID().toString().replace("-", "")
//    val amount = BigDecimal.valueOf(10).setScale(2, 2)
//
//    val supportBanks = listOf(
//            SurePayConfig.SupportBank(bank = Bank.MBB, bankCode = "10000628")
//    )
//
//    val config = SurePayConfig(supportBanks = supportBanks)
//
//    val customer =  "1_1"
//
//    val signParam = "${config.merchantId}${amount}${orderId}${customer}${config.apiKey}${config.currency}${config.clientIp}"
//    val token =  DigestUtils.md5Hex(signParam)
//
//    println("orderId=$orderId")
//    println("token=$token")
//}