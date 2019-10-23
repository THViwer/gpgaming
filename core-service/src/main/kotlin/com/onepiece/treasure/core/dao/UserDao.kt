package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.model.User

interface UserDao {

    fun get(id: Int): User

}