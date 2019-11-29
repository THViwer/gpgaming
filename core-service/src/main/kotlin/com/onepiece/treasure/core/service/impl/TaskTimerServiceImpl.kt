package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.model.TaskTimerType
import com.onepiece.treasure.core.dao.TaskTimerDao
import com.onepiece.treasure.core.service.TaskTimerService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TaskTimerServiceImpl(
        private val taskTimerDao: TaskTimerDao
) : TaskTimerService {

    override fun lock(day: LocalDate, type: TaskTimerType): Boolean {
        return taskTimerDao.lock(day = day, type = type)
    }

    override fun done(day: LocalDate, type: TaskTimerType): Boolean {
        return taskTimerDao.done(day = day, type = type)
    }

    override fun fail(day: LocalDate, type: TaskTimerType): Boolean {
        return taskTimerDao.fail(day = day, type = type)
    }
}