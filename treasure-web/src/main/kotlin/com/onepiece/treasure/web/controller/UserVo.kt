package com.onepiece.treasure.web.controller


data class UserVo(

        // 用户IOd
        val id: Int,

        // 用户名
        val username: String,

        // token
        var token: String? = null

)
