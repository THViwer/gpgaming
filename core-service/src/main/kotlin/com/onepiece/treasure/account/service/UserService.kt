package com.onepiece.treasure.account.service

import com.onepiece.treasure.account.model.User

interface UserService {

    fun get(id: Int): User

}