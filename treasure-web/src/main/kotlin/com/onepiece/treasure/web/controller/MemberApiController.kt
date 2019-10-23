package com.onepiece.treasure.web.controller

import com.onepiece.treasure.core.model.enums.Status
import com.onepiece.treasure.web.controller.basic.BasicController
import com.onepiece.treasure.web.controller.value.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/member")
class MemberApiController : BasicController(), MemberApi {

    @GetMapping
    override fun query(
            @RequestParam(value = "id") id: Int,
            @RequestParam(value = "username", required = false) username: String?,
            @RequestParam(value = "levelId", required = false) levelId: Int?,
            @RequestParam(value = "status", required = false) status: Status?,
            @RequestParam(defaultValue = "0") current: Int,
            @RequestParam(defaultValue = "10") size: Int
    ): MemberPage {

        val query = MemberQuery(id = id, username = username, levelId = levelId, status = status,
                current = current, size = size)

        return MemberValueFactory.generatorMemberPage()
    }

    @PutMapping
    override fun change(
            @RequestBody memberUo: MemberUo
    ) {

    }

    @GetMapping("/balance/{memberId}")
    override fun balance(@PathVariable("memberId") memberId: Int): BalanceDetail {
        return BalanceValueFactory.generatorBalanceDetail()
    }
}