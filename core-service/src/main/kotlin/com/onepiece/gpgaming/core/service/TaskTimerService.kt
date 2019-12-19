package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.TaskTimerType
import java.time.LocalDate

interface TaskTimerService {

    fun lock(day: LocalDate, type: TaskTimerType): Boolean

    fun done(day: LocalDate, type: TaskTimerType): Boolean

    fun fail(day: LocalDate, type: TaskTimerType): Boolean

}