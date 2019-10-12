package com.onepiece.treasure.account.dao

import com.onepiece.treasure.account.model.User

interface UserDao {

    fun get(id: Int): User

}