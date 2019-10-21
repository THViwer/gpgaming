package com.onepiece.treasure.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.account.service.UserService
import com.onepiece.treasure.jwt.AuthService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
        val userService: UserService,
        val authService: AuthService,
        val objectMapping: ObjectMapper
): UserApi {

    @GetMapping("/{id}")
    override  fun get(@PathVariable("id") id: Int): UserVo {
        val user =  userService.get(id)

        //TODO check hash
        val token = authService.login(user.id).token
        return UserVo(id = id, username = user.name, token = token)
    }

}