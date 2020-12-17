package com.onepiece.gpgaming.player.sms

import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


object EmailSMTPService {
    // for example, smtp.mailgun.org
    private const val SMTP_SERVER = "ssl://smtp.zoho.com"
    private const val USERNAME = "team@unclejay.com"
    private const val PASSWORD = "Unclejay@888"

    private const val EMAIL_FROM = "team@unclejay.com"
    private const val EMAIL_TO = "cabbage.cgh@gmail.com"
    private const val EMAIL_TO_CC = ""

    private const val EMAIL_SUBJECT = "Test Send Email via SMTP"
    private const val EMAIL_TEXT = "Hello Java Mail \n ABC123"

    fun send() {
        val prop = System.getProperties()
        prop["mail.smtp.host"] = SMTP_SERVER //optional, defined in SMTPTransport
        prop["mail.smtp.auth"] = "true"
        prop["mail.smtp.port"] = "456" // default port 25

        val session = Session.getInstance(prop, null)
        val msg = MimeMessage(session)

        // from
        msg.setFrom(InternetAddress(EMAIL_FROM))

        // to
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_TO, false))

        // cc
        msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(EMAIL_TO_CC, false))

        // subject
        msg.subject = EMAIL_SUBJECT

        // content
        msg.setText(EMAIL_TEXT)
        msg.sentDate = Date()

        // Get SMTPTransport
        val t = session.getTransport("smtp")

        // connect
        t.connect(SMTP_SERVER, USERNAME, PASSWORD)

        // send
        t.sendMessage(msg, msg.allRecipients)
        t.close()
    }

}

fun main() {
    EmailSMTPService.send()
}