package com.onepiece.treasure.web.controller

import com.onepiece.treasure.account.model.enums.Status
import com.onepiece.treasure.web.controller.basic.BasicController
import com.onepiece.treasure.web.controller.value.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/waiter")
class WaiterApiController : BasicController(), WaiterApi {

    @GetMapping
    override fun query(
            @RequestParam(value = "username", required = false) username: String?,
            @RequestParam(value = "name", required = false) name: String?,
            @RequestParam(value = "status", required = false) status: Status?
    ): List<WaiterVo> {
        return WaiterValueFactory.generatorWaiters()
    }

    @PostMapping
    override fun create(@RequestBody waiterCo: WaiterCo) {
    }

    @PutMapping
    override fun update(@RequestBody waiterUo: WaiterUo) {
    }

    @GetMapping("/permission/{memberId}")
    override fun permission(@PathVariable("memberId") memberId: Int): List<PermissionVo> {
        return PermissionValueFactory.generatorPermissionVo()
    }
}