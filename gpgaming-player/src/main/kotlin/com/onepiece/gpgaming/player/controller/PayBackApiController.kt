package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.core.service.PayOrderService
import org.slf4j.LoggerFactory
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

        /**
         *
         *  2020-03-30 18:36:48.536  INFO 22412 --- [nio-8002-exec-3] c.o.g.p.controller.PayBackApiController  : MerchantCode=T004
        2020-03-30 18:36:48.536  INFO 22412 --- [nio-8002-exec-3] c.o.g.p.controller.PayBackApiController  : RefNo=fcf8ad055e
        2020-03-30 18:36:48.537  INFO 22412 --- [nio-8002-exec-3] c.o.g.p.controller.PayBackApiController  : Amount=10.00
        2020-03-30 18:36:48.537  INFO 22412 --- [nio-8002-exec-3] c.o.g.p.controller.PayBackApiController  : TransID=902644
        2020-03-30 18:36:48.537  INFO 22412 --- [nio-8002-exec-3] c.o.g.p.controller.PayBackApiController  : Status=1
        2020-03-30 18:36:48.537  INFO 22412 --- [nio-8002-exec-3] c.o.g.p.controller.PayBackApiController  : ErrDesc=
        2020-03-30 18:36:48.537  INFO 22412 --- [nio-8002-exec-3] c.o.g.p.controller.PayBackApiController  : Signature=cc312120acae5a2605869e78149479b2dccedeb7bb65f24d20edf0e76330514b
         */

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


        val merchantCode = request.getParameter("MerchantCode")
        val orderId = request.getParameter("RefNo")
        val amount = request.getParameter("Amount")
        val thirdOrderId = request.getParameter("TransID")
        val status = request.getParameter("Status")
        val signature = request.getParameter("Signature")

        // check sign
        payOrderService.successful(orderId = orderId, thirdOrderId = thirdOrderId)
    }
}