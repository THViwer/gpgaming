package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.controller.basic.BasicController
import com.onepiece.treasure.controller.value.*
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/cash")
class CashApiController : BasicController(), CashApi {

    @GetMapping("/bank")
    override fun banks(): List<MemberBankVo> {
        return MemberBankValueFactory.generatorMemberBanks()
    }

    @PostMapping("/bank")
    override fun bankCreate(@RequestBody memberBankCo: MemberBankCo) {
    }

    @PutMapping("/bank")
    override fun bankUpdate(@RequestBody memberBankUo: MemberBankUo) {
    }

    @GetMapping("/topUp")
    override fun topUp(
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: DepositState?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endTime") endTime: LocalDateTime,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): CashDepositPage {
        return CashValueFactory.generatorCashDepositPage()
    }


    @PutMapping("/topUp")
    override fun topUp(@RequestBody cashTopUpReq: CashDepositReq): CashDepositResp {
        return CashDepositResp(orderId = UUID.randomUUID().toString())
    }

    @GetMapping("/withdraw")
    override fun withdraw(
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: DepositState?,
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