package com.onepiece.gpgaming.player.sms

interface EmailSMTPService {

    fun send(username: String, email: String)

}