package com.onepiece.treasure.core.service

import com.onepiece.treasure.core.dao.UserDao
import com.onepiece.treasure.core.model.User
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
        private val userDao: UserDao,
        private val redisService: RedisService
) : UserService {


    override fun get(id: Int): User {
        return redisService.get("user:$id", User::class.java) {
            userDao.get(id)
        }!!
    }
}