package com.example.common.mail

import com.example.users.getLogger
import jakarta.mail.MessagingException
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

interface MailClient {
    fun send(to: String, title: String, htmlContent: String)

    fun sendFromTemplate(to: String, title: String, template: String, context: Context? = Context())
}

@Service
@ConditionalOnProperty("spring.mail.host")
class MailService(val javaMailSender: JavaMailSender, val templateEngine: SpringTemplateEngine) :
    MailClient {

    val logger = getLogger()

    override fun send(to: String, title: String, htmlContent: String) {
        logger.debug("sending email to={}, title={}", to, title)
        try {
            val mimeMessage =
                with(MimeMessageHelper(javaMailSender.createMimeMessage())) {
                    setTo(to)
                    setSubject(title)
                    setText(htmlContent, true)
                    this.mimeMessage
                }
            javaMailSender.send(mimeMessage)
            logger.info("Sent email to={}, title={}", to, title)
        } catch (ex: MailException) {
            logger.error("failed to send email to={}", to, ex)
        } catch (ex: MessagingException) {
            logger.error("failed to send email to={}", to, ex)
        }
    }

    override fun sendFromTemplate(to: String, title: String, template: String, context: Context?) =
        send(to, title, templateEngine.process(template, context))
}
