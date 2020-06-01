package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.core.service.PayOrderService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


@RestController
@RequestMapping("/pay")
class PayBackApiController(
        private val payOrderService: PayOrderService
) : PayBackApi {

    val log = LoggerFactory.getLogger(PayBackApiController::class.java)

    @RequestMapping("/m3pay")
    override fun m3pay() {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request

        log.info("--------------------------------")
        log.info("--------------------------------")
        log.info("请求方式：${request.method}")
        log.info("m3pay 开始解析")

        log.info("url param: ")
        log.info("MerchantCode=${request.getParameter("MerchantCode")}")
        log.info("RefNo=${request.getParameter("RefNo")}")
        log.info("Amount=${request.getParameter("Amount")}")
        log.info("TransID=${request.getParameter("TransID")}")
        log.info("Status=${request.getParameter("Status")}")
        log.info("ErrDesc=${request.getParameter("ErrDesc")}")
        log.info("Signature=${request.getParameter("Signature")}")
        log.info("S_bankID=${request.getParameter("S_bankID")}")
        log.info("--------------------------------")
        log.info("--------------------------------")

        val merchantCode = request.getParameter("MerchantCode")
        val orderId = request.getParameter("RefNo")
        val amount = request.getParameter("Amount")
        val thirdOrderId = request.getParameter("TransID")
        val status = request.getParameter("Status")
        val signature = request.getParameter("Signature")

        // check sign

        try {
            if (status == "0") {
                payOrderService.failed(orderId = orderId)
            } else {
                payOrderService.successful(orderId = orderId, thirdOrderId = thirdOrderId)
            }
        } catch (e: Exception) {
            log.info("支付请求失败", e)
        }
    }

    @RequestMapping("/surepay")
    override fun surepay() {

        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request

        log.info("--------------------------------")
        log.info("--------------------------------")
        log.info("请求方式：${request.method}")
        log.info("surepay 开始解析")

        log.info("url param: ")
        log.info("merchant=${request.getParameter("merchant")}")
        log.info("amount=${request.getParameter("amount")}")
        log.info("refid=${request.getParameter("refid")}")
        log.info("customer=${request.getParameter("customer")}")
        log.info("clientip=${request.getParameter("clientip")}")
        log.info("token=${request.getParameter("token")}")
        log.info("trxno=${request.getParameter("trxno")}")
        log.info("status=${request.getParameter("status")}")
        log.info("status_message=${request.getParameter("status_message")}")
        log.info("surepay 解析结束")
        log.info("--------------------------------")
        log.info("--------------------------------")

        val refid = request.getParameter("refid")
        val status = request.getParameter("status")
        val trxno = request.getParameter("trxno")


        try {
            if (status == "1") {
                payOrderService.successful(orderId = refid, thirdOrderId = trxno)
            } else {
                payOrderService.failed(orderId = refid)
            }
        } catch (e: Exception) {
            log.info("支付请求失败", e)
        }
    }

    @GetMapping("/gppay")
    override fun gppay() {

        //         val req = MerchantNotifyReq(orderId = mOrderId, state = order.state, merchantCode = merchant.code, timestamp = System.currentTimeMillis() / 1000,
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request

        val orderId = request.getParameter("orderId")
        val state = request.getParameter("state")
        val merchantCode = request.getParameter("merchantCode")
        val timestamp = request.getParameter("timestamp")
        val sign = request.getParameter("sign")

        log.info("gppay获得通知订单: orderId=${orderId}, state=${state}, merchantCode=${merchantCode}, timestamp=${timestamp}, sign=${sign}")

        if (state == "Successful") {
            payOrderService.successful(orderId = orderId, thirdOrderId = orderId)
        } else {
            payOrderService.failed(orderId = orderId)
        }



    }
}