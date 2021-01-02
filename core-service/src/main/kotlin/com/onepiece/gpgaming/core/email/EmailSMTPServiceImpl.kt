package com.onepiece.gpgaming.core.email

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.*
import javax.activation.DataHandler
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


@Service
open class EmailSMTPServiceImpl: EmailSMTPService {
    // for example, smtp.mailgun.org
    private val smtp_server = "smtppro.zoho.com"
    private val auth_username = "team@unclejay.com"
    private val auth_password = "Unclejay@888"

    private val from = "team@unclejay.com"
    private val EMAIL_TO_CC = ""

    private val title = "Thanks for signing up with UNCLE JAY!"



    @Async
    override fun send(clientId: Int, username: String, email: String) {
        val content = EmailContent.formRegisterContent(username = username)

        when (clientId) {
            10001 -> { this.sendEmail(email = email, content = content) }
            else -> {  }
        }
    }

    @Async
    override fun firstDepositSend(clientId: Int, username: String, email: String) {
        val content = EmailContent.firstDeposit()
        when (clientId) {
            10001 -> { this.sendEmail( email = email, content = content) }
            else -> {}
        }
    }

    private fun sendEmail(email: String, content: String) {
        val prop = System.getProperties()
//        prop.setProperty("mail.smtp.auth", "true");//开启认证
//        prop.setProperty("mail.debug", "true");//启用调试
        prop.setProperty("mail.smtp.timeout", "200000");//设置链接超时
        prop.setProperty("mail.smtp.port", "25");//设置端口
        prop.setProperty("mail.smtp.socketFactory.port", "465");//设置ssl端口
        prop.setProperty("mail.smtp.socketFactory.fallback", "false");
        prop.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        val session = Session.getInstance(prop, null)
        val msg = MimeMessage(session)

        // from
        msg.setFrom(InternetAddress(from))

        // to
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false))

        // cc
        msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(EMAIL_TO_CC, false))

        // subject
        msg.subject = title

        // content
//        msg.setText(getHtmlContent(username = username))
        msg.dataHandler = DataHandler(HTMLDataSource(content))
        msg.sentDate = Date()

        // Get SMTPTransport
        val t = session.getTransport("smtp")

        // connect
        t.connect(smtp_server, auth_username, auth_password)

        // send
        t.sendMessage(msg, msg.allRecipients)
        t.close()
    }
}


//fun main() {
//    EmailSMTPService.send("cabbage", "cabbage.cgh@gmail.com")
//}