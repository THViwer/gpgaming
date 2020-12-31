package com.onepiece.gpgaming.core.email

interface EmailSMTPService {

    fun send(username: String, email: String)

    fun firstDepositSend(username: String, email: String)

}