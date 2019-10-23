package com.onepiece.treasure.web.controller

import com.onepiece.treasure.core.model.enums.Status
import com.onepiece.treasure.web.controller.basic.BasicController
import com.onepiece.treasure.web.controller.value.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/level")
class LevelApiController : BasicController(), LevelApi {

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

    @PutMapping("/move")
    override fun move(@RequestBody levelMoveDo: LevelMoveDo): LevelMoveVo {
        return LevelValueFactory.generatorLevelMoveVo()
    }

    @GetMapping("/move/check/{sequence}")
    override fun checkMove(@PathVariable sequence: String): LevelMoveCheckVo {
        return LevelValueFactory.generatorLevelMoveCheckVo()
    }
}