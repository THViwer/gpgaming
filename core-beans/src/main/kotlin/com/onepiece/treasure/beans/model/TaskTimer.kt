package com.onepiece.treasure.beans.model

import java.time.LocalDate
import java.time.LocalDateTime

data class TaskTimer(

        val id: Int,

        val day: LocalDate,

        val state: TaskTimerState,

        val type: TaskTimerType,

        val createdTime: LocalDateTime,

        val updatedTime: LocalDateTime

)

enum class TaskTimerType {

    MemberPlatformDaily,

    MemberDaily,

    ClientPlatformDaily,

    ClientDaily

}

enum class TaskTimerState {
    Process,

    Done,

    Fail
}