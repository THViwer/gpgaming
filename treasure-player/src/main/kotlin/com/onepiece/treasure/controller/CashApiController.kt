package com.onepiece.treasure.controller

import com.onepiece.treasure.core.model.enums.TopUpState
import com.onepiece.treasure.controller.basic.BasicController
import com.onepiece.treasure.controller.value.*
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/cash")
class CashApiController : BasicController(), CashApi {

    @GetMapping("/topUp")
    override fun topUp(
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: TopUpState?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startCreatedTime") startCreatedTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endCreatedTime") endCreatedTime: LocalDateTime,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): CashTopUpPage {
        return CashValueFactory.generatorCashTopUpPage()
    }


    @PutMapping("/topUp")
    override fun topUp(@RequestBody cashTopUpReq: CashTopUpReq): CashTopUpResp {
        return CashTopUpResp(orderId = UUID.randomUUID().toString())
    }

    @GetMapping("/withdraw")
    override fun withdraw(
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: TopUpState?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startCreatedTime") startCreatedTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endCreatedTime") endCreatedTime: LocalDateTime,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): CashWithdrawPage {
        return CashValueFactory.generatorCashWithdrawPage()
    }

    @PutMapping("/withdraw")
    override fun withdraw(@RequestBody cashWithdrawReq: CashWithdrawReq): CashWithdrawResp {
        return CashWithdrawResp(orderId = UUID.randomUUID().toString())
    }

    @PutMapping("/transfer")
    override fun transfer(@RequestBody cashTransferReq: CashTransferReq) {
    }
}