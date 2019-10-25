package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime
import java.util.*

object LevelValueFactory {

    fun generatorAll(): List<LevelVo> {

        val now = LocalDateTime.now()

        val v1 = LevelVo(id = 1, name = "默认", total = 10213, status = Status.Normal, createdTime = now)
        val v2 = v1.copy(id = 2, name = "vip", total = 205)
        val v3 = v1.copy(id = 3, name = "正常", total = 2315)
        val v4 = v1.copy(id = 4, name = "停用", total = 0, status = Status.Stop)

        return listOf(v1, v2, v3, v4)
    }

    fun generatorLevelMoveVo(): LevelMoveVo {
        return LevelMoveVo(sequence = UUID.randomUUID().toString())
    }

    fun generatorLevelMoveCheckVo(): LevelMoveCheckVo {
        return LevelMoveCheckVo(done = true)
    }

}

data class LevelVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("名称")
        val name: String,

        @ApiModelProperty("总人数")
        val total: Int,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime
)

data class LevelUo(
        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("名称")
        val name: String?,

        @ApiModelProperty("状态")
        val status: Status?
)

data class LevelCo(

        @ApiModelProperty("名称")
        val name: String?,

        @ApiModelProperty("状态")
        val status: Status?
)

data class LevelMoveDo(

        @ApiModelProperty("充值金额")
        val totalTopUp: Int,

        @ApiModelProperty("存款次数")
        val totalTopUpSequence: Int

)

data class LevelMoveVo(

        @ApiModelProperty("执行序列")
        val sequence: String
)

data class LevelMoveCheckVo(

        @ApiModelProperty("是否执行完成")
        val done: Boolean
)