package com.onepiece.treasure.core.service

import com.onepiece.treasure.core.model.User

interface UserService {

    fun get(id: Int): User

}