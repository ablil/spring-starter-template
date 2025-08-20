package com.example.users

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import org.openapitools.api.AccountsApi
import org.openapitools.model.ChangePasswordRequest
import org.openapitools.model.SignUpRequest
import org.openapitools.model.UpdateUserInformationRequest
import org.openapitools.model.UserInfo
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountsResource(val accountService: AccountService) : AccountsApi {

    override fun activateAccount(key: String): ResponseEntity<Unit> =
        accountService.activateAccount(key).let { ResponseEntity.noContent().build() }

    @PreAuthorize("principal.username == #username")
    override fun changePassword(
        username: String,
        changePasswordRequest: ChangePasswordRequest,
    ): ResponseEntity<Unit> =
        accountService
            .changePassword(
                changePasswordRequest.currentPassword,
                changePasswordRequest.newPassword,
            )
            .let { ResponseEntity.noContent().build() }

    override fun getCurrentAccount(): ResponseEntity<UserInfo> =
        ResponseEntity.ok(accountService.getAuthenticatedUser().toUserInfo())

    @PreAuthorize("principal.username == #username")
    override fun updateUserInformation(
        username: String,
        updateUserInformationRequest: UpdateUserInformationRequest,
    ): ResponseEntity<Unit> {
        return accountService.updateUserInfo(UserInfoDTO.from(updateUserInformationRequest)).let {
            ResponseEntity.noContent().build()
        }
    }
}

data class RegistrationDTO(
    @field:Size(min = 6) val username: String,
    @field:Email val email: String,
    @field:Size(min = 10) val password: String,
) {
    companion object {
        fun from(request: SignUpRequest): RegistrationDTO =
            RegistrationDTO(
                username = request.username,
                email = request.email,
                password = request.password,
            )
    }
}

data class UserInfoDTO(
    val firstName: String?,
    val lastName: String?,
    @field:Email val email: String,
) {
    companion object {
        fun from(request: UpdateUserInformationRequest): UserInfoDTO =
            UserInfoDTO(
                firstName = request.firstName,
                lastName = request.lastName,
                email = request.email,
            )
    }
}

fun DomainUser.toUserInfo(): UserInfo =
    UserInfo(
        id = this.id,
        username = this.username,
        email = this.email,
        disabled = !this.isActive(),
        roles = this.roles.map { it.name }.toMutableList(),
        firstName = this.firstName,
        lastName = this.lastName,
        fullName = this.fullName,
    )
