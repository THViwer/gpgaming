package com.onepiece.gpgaming.payment

import com.onepiece.gpgaming.beans.enums.PayType
import org.springframework.stereotype.Service

@Service
class PayGateway(
        private val m3PayService: M3PayService,
        private val surePayService: SurePayService,
        private val gpPayService: GPPayService

) : PayService {

    override fun start(req: PayRequest): Map<String, Any> {

        return  when (req.payType) {
            PayType.M3Pay -> m3PayService.start(req)
            PayType.SurePay -> surePayService.start(req)
            PayType.GPPay -> gpPayService.start(req)
        }
    }
}