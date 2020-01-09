package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDate
import java.time.LocalDateTime

data class TaskTimer(

        val id: Int,

        val day: LocalDate,

        val state: TaskTimerState,

        val type: TaskTimerType,

        val createdTime: LocalDateTime,

        val updatedTime: LocalDateTime,

        // 状态
        val status: Status

)

enum class TaskTimerType {

    MemberPlatformDaily,

    MemberDaily,

    ClientPlatformDaily,

    ClientDaily,

    PromotionPlatformDaily,

    PromotionDaily

}

enum class TaskTimerState {
    Process,

    Done,

    Fail
}