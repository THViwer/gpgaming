package com.onepiece.treasure.web.controller

import com.onepiece.treasure.account.model.enums.Status
import com.onepiece.treasure.web.controller.value.LevelCo
import com.onepiece.treasure.web.controller.value.LevelUo
import com.onepiece.treasure.web.controller.value.LevelValueFactory
import com.onepiece.treasure.web.controller.value.LevelVo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/level")
class LevelApiController : LevelApi {

    @GetMapping
    override fun all(): List<LevelVo> {
        return LevelValueFactory.generatorAll()
    }

    @GetMapping("/normal")
    override fun normalList(): List<LevelVo> {
        return LevelValueFactory.generatorAll().filter { it.status == Status.Normal }
    }

    @PostMapping
    override fun create(@RequestBody levelCo: LevelCo) {
    }

    @PutMapping
    override fun update(@RequestBody levelUo: LevelUo) {
    }
}