package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.TaskTimer
import com.onepiece.gpgaming.beans.model.TaskTimerType
import com.onepiece.gpgaming.core.dao.basic.BasicDao
import java.time.LocalDate

interface TaskTimerDao: BasicDao<TaskTimer> {

    fun lock(day: LocalDate, type: TaskTimerType): Boolean

    fun done(day: LocalDate, type: TaskTimerType): Boolean

    fun fail(day: LocalDate, type: TaskTimerType): Boolean

}