package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.TaskTimerType
import java.time.LocalDate

interface TaskTimerService {

    fun lock(day: LocalDate, type: TaskTimerType): Boolean

    fun done(day: LocalDate, type: TaskTimerType): Boolean

    fun fail(day: LocalDate, type: TaskTimerType): Boolean

}