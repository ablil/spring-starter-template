package com.example.common

import com.example.users.DomainUser
import com.example.users.getLogger
import jakarta.mail.MessagingException
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Service
class MailService(val javaMailSender: JavaMailSender, val templateEngine: SpringTemplateEngine) {

    val logger = getLogger()

    @Value("\${spring.application.name}") lateinit var applicationName: String

    @Value("\${example.application.urls.activation-link}") lateinit var activationLink: String

    @Value("\${example.application.urls.reset-password-link}") lateinit var resetLink: String

    fun send(to: String, title: String, htmlContent: String) {
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

    fun sendFromTemplate(
        to: String,
        title: String,
        template: String,
        context: Context? = Context(),
    ) = send(to, title, templateEngine.process(template, context))

    fun sendAccountRegistrationEmail(user: DomainUser) =
        sendFromTemplate(
            to = user.email,
            title = "Account registration",
            template = "accountCreated",
            context =
                Context().apply {
                    setVariables(
                        mapOf(
                            "applicationName" to applicationName,
                            "fullName" to user.fullName,
                            "activationLink" to
                                "%s?key=%s"
                                    .format(activationLink, requireNotNull(user.activationKey)),
                        )
                    )
                },
        )

    @Async
    fun sendAccountActivationEmail(user: DomainUser) =
        sendFromTemplate(
            to = user.email,
            title = "Account activation",
            template = "accountActivated",
            context = Context().apply { setVariable("fullName", user.fullName) },
        )

    fun sendPasswordResetLinkEmail(user: DomainUser) =
        sendFromTemplate(
            to = user.email,
            title = "Reset your password",
            template = "resetPassword",
            context =
                Context().apply {
                    setVariable(
                        "resetLink",
                        "%s?key=%s".format(resetLink, requireNotNull(user.resetKey)),
                    )
                },
        )

    @Async
    fun sendPasswordChangedEmail(user: DomainUser) =
        sendFromTemplate(
            to = user.email,
            title = "Your password has changed",
            template = "passwordChanged",
            context = Context().apply { setVariable("applicationName", applicationName) },
        )
}
