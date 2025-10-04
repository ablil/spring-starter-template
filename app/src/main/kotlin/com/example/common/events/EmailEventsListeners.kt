package com.example.common.events

import com.example.common.configs.ApplicationUrls
import com.example.common.entities.DomainUser
import com.example.common.mail.MailClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.thymeleaf.context.Context

class PasswordChangedEvent(val user: DomainUser) : ApplicationEvent(user)

class AccountCreatedEvent(val user: DomainUser) : ApplicationEvent(user)

class AccountActivatedEvent(val user: DomainUser) : ApplicationEvent(user)

class PasswordResetRequested(val user: DomainUser) : ApplicationEvent(user)

@Component
@Async
class EmailEventsListeners(
    val mailService: MailClient,
    val applicationUrls: ApplicationUrls,
    @Value("\${spring.application.name}") val applicationName: String,
) {

    @EventListener
    fun accountPasswordChanged(event: PasswordChangedEvent) {
        mailService.sendFromTemplate(
            to = event.user.email,
            title = "Your password has changed",
            template = "passwordChanged",
            context = Context().apply { setVariable("applicationName", applicationName) },
        )
    }

    @EventListener
    fun accountCreated(event: AccountCreatedEvent) {
        mailService.sendFromTemplate(
            to = event.user.email,
            title = "Account registration",
            template = "accountCreated",
            context =
                Context().apply {
                    setVariables(
                        mapOf(
                            "applicationName" to applicationName,
                            "fullName" to event.user.fullName,
                            "activationLink" to
                                "%s?key=%s"
                                    .format(
                                        applicationUrls.activationLink,
                                        requireNotNull(event.user.activationKey),
                                    ),
                        )
                    )
                },
        )
    }

    @EventListener
    fun accountActivated(event: AccountActivatedEvent) {
        mailService.sendFromTemplate(
            to = event.user.email,
            title = "Account activation",
            template = "accountActivated",
            context = Context().apply { setVariable("fullName", event.user.fullName) },
        )
    }

    @EventListener
    fun requestedPasswordReset(event: PasswordResetRequested) {
        mailService.sendFromTemplate(
            to = event.user.email,
            title = "Reset your password",
            template = "resetPassword",
            context =
                Context().apply {
                    setVariable(
                        "resetLink",
                        "%s?key=%s"
                            .format(
                                applicationUrls.resetPasswordLink,
                                requireNotNull(event.user.resetKey),
                            ),
                    )
                },
        )
    }
}
