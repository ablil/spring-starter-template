package com.example.users

import com.example.common.configs.AuthorityConstants
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import java.net.URI
import org.openapitools.api.UsersApi
import org.openapitools.model.CreateUserRequest
import org.openapitools.model.GetAllUsers200Response
import org.openapitools.model.GetAllUsers200ResponseContentInner
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

const val DEFAULT_PAGE_NUMBER = 0
const val DEFAULT_PAGE_SIZE = 10
const val DEFAULT_SORT_FIELD = "id"

@RestController
@AdminOnly
class UsersResource(val userService: UserService) : UsersApi {

    override fun getAllUsers(
        page: Int?,
        size: Int?,
        sort: String?,
        by: String?,
    ): ResponseEntity<GetAllUsers200Response> {
        val pageableResult =
            userService.getAllUsers(
                PageRequest.of(
                    page ?: DEFAULT_PAGE_NUMBER,
                    size ?: DEFAULT_PAGE_SIZE,
                    Sort.Direction.DESC.takeIf { sort?.lowercase() == "desc" }
                        ?: Sort.DEFAULT_DIRECTION,
                    by ?: DEFAULT_SORT_FIELD,
                )
            )
        return ResponseEntity.ok(
            GetAllUsers200Response(
                content = pageableResult.map { it.toResponse() }.toMutableList(),
                page = pageableResult.number,
                propertySize = pageableResult.size,
                total = pageableResult.totalElements.toInt(),
            )
        )
    }

    override fun getUser(username: String): ResponseEntity<GetAllUsers200ResponseContentInner> =
        ResponseEntity.ofNullable(userService.getUser(username)?.toResponse())

    override fun createUser(
        createUserRequest: CreateUserRequest
    ): ResponseEntity<GetAllUsers200ResponseContentInner> =
        userService.createUser(CreateOrUpdateUserDTO.from(createUserRequest)).let {
            ResponseEntity.created(URI("/api/v1/users/${it.username}")).body(it.toResponse())
        }

    override fun updateUser(
        username: String,
        createUserRequest: CreateUserRequest,
    ): ResponseEntity<GetAllUsers200ResponseContentInner> =
        ResponseEntity.ofNullable(
            userService
                .updateUserInfo(username, CreateOrUpdateUserDTO.from(createUserRequest))
                ?.toResponse()
        )

    override fun deleteUser(username: String): ResponseEntity<Unit> =
        userService.deleteUser(username).let { ResponseEntity.noContent().build() }
}

fun DomainUser.toResponse(): GetAllUsers200ResponseContentInner {
    return GetAllUsers200ResponseContentInner(
        id = this.id,
        username = this.username,
        email = this.email,
        disabled = !this.isActive(),
        firstName = this.firstName,
        lastName = this.lastName,
        fullName = this.fullName,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        createdBy = this.updatedBy,
        updatedBy = this.updatedBy,
        roles = this.roles.map { it.name }.toMutableList(),
    )
}

data class CreateOrUpdateUserDTO(
    val firstName: String?,
    val lastName: String?,
    val roles: Set<AuthorityConstants>?,
    @field:Email val email: String,
    @field:Size(min = 6) val username: String,
) {
    companion object {
        fun from(request: CreateUserRequest): CreateOrUpdateUserDTO =
            CreateOrUpdateUserDTO(
                firstName = request.firstName,
                lastName = request.lastName,
                roles = request.roles?.map { AuthorityConstants.valueOf(it) }?.toMutableSet(),
                email = request.email,
                username = request.username,
            )
    }
}
