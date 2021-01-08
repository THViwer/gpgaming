package com.onepiece.gpgaming.web.controller.value

data class EmailReq(

        val memberIds: List<Int>,

        val content: String,

        val smtp_server: String = "smtppro.zoho.com",

        val auth_username: String = "team@unclejay.com",

        val auth_password: String = "Unclejay@888"

)