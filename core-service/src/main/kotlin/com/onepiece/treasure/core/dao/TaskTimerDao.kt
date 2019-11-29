package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.TaskTimer
import com.onepiece.treasure.beans.model.TaskTimerType
import com.onepiece.treasure.core.dao.basic.BasicDao
import java.time.LocalDate

interface TaskTimerDao: BasicDao<TaskTimer> {

    fun lock(day: LocalDate, type: TaskTimerType): Boolean

    fun done(day: LocalDate, type: TaskTimerType): Boolean

    fun fail(day: LocalDate, type: TaskTimerType): Boolean

}