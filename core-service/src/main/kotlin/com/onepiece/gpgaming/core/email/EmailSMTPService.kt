package com.onepiece.gpgaming.core.email

interface EmailSMTPService {

    fun send(clientId: Int, username: String, email: String)

    fun firstDepositSend(clientId: Int, username: String, email: String)

    fun sends(emails: String, content: String, smtp_server: String, auth_username: String, auth_password: String)

}