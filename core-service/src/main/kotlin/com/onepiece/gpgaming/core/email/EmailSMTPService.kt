package com.onepiece.gpgaming.core.email

interface EmailSMTPService {

    fun send(clientId: Int, username: String, email: String)

    fun firstDepositSend(clientId: Int, username: String, email: String)

}