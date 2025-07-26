package com.example.users

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.openapitools.api.AccountApi
import org.openapitools.model.ChangePasswordRequest
import org.openapitools.model.SignUpRequest
import org.openapitools.model.UpdateUserInformationRequest
import org.openapitools.model.UserInfo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountsResource(val accountService: AccountService) : AccountApi {

    override fun activateAccount(key: String): ResponseEntity<Unit> =
        accountService.activateAccount(key).let { ResponseEntity.noContent().build() }

    override fun changePassword(
        changePasswordRequest: ChangePasswordRequest
    ): ResponseEntity<Unit> =
        accountService
            .changePassword(
                changePasswordRequest.currentPassword,
                changePasswordRequest.newPassword,
            )
            .let { ResponseEntity.noContent().build() }

    override fun getCurrentUser(): ResponseEntity<UserInfo> =
        ResponseEntity.ok(accountService.getAuthenticatedUser().toUserInfo())

    override fun updateUserInformation(
        updateUserInformationRequest: UpdateUserInformationRequest
    ): ResponseEntity<Unit> =
        accountService.updateUserInfo(UserInfoDTO.from(updateUserInformationRequest)).let {
            ResponseEntity.noContent().build()
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

data class EmailWrapper(@field:Email val email: String)

data class KeyAndPassword(
    @field:NotBlank val resetKey: String,
    @field:Size(min = 10) val password: String,
)

data class ChangePasswordDTO(
    @field:NotBlank val currentPassword: String,
    @field:Size(min = 10) val newPassword: String,
)

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
        disabled = this.disabled,
        roles = this.roles.map { it.name }.toMutableList(),
        firstName = this.firstName,
        lastName = this.lastName,
        fullName = this.fullName,
    )
