package com.onepiece.treasure.controller

import com.onepiece.treasure.account.dao.UserDao
import com.onepiece.treasure.account.model.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
        val userDao: UserDao
) {

    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: Int): User {
        return userDao.get(id)
    }
}