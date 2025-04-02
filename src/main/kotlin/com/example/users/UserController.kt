package com.example.users

import com.example.common.AuthorityConstants
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Pattern.Flag
import jakarta.validation.constraints.Size
import java.net.URI
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

const val DEFAULT_PAGE_NUMBER = 0
const val DEFAULT_PAGE_SIZE = 10

@RestController
@RequestMapping("/api/users")
@AdminOnly
class UserController(val userService: UserService) {

    @GetMapping
    fun getAllUsers(
        @RequestParam("page", defaultValue = DEFAULT_PAGE_NUMBER.toString())
        @Min(0)
        page: Int = DEFAULT_PAGE_NUMBER,
        @RequestParam("size", defaultValue = DEFAULT_PAGE_SIZE.toString())
        @Min(0)
        size: Int = DEFAULT_PAGE_SIZE,
        @RequestParam("sort", defaultValue = "asc")
        @Pattern(regexp = "(asc|desc)", flags = [Pattern.Flag.CASE_INSENSITIVE])
        sort: String = "asc",
        @RequestParam("by", defaultValue = "id")
        @Pattern(
            regexp = "(id|username|email|createdAt|updatedAt)",
            flags = [Flag.CASE_INSENSITIVE],
        )
        by: String = "id",
    ): ResponseEntity<PageableResult<DomainUser>> =
        ResponseEntity.ok(
            userService
                .getAllUsers(
                    PageRequest.of(
                        page,
                        size,
                        Sort.Direction.DESC.takeIf { sort.lowercase() == "desc" }
                            ?: Sort.DEFAULT_DIRECTION,
                        by,
                    )
                )
                .let { PageableResult(it.content, it.number, it.size, it.totalElements) }
        )

    @GetMapping("{username}")
    fun getUser(@PathVariable("username") username: String): ResponseEntity<DomainUser> =
        ResponseEntity.ofNullable(userService.getUser(username))

    @PostMapping
    fun createUser(@RequestBody @Valid body: CreateOrUpdateUserDTO): ResponseEntity<DomainUser> =
        userService.createUser(body).let {
            ResponseEntity.created(URI("/api/users/${it.username}")).body(it)
        }

    @PutMapping("{username}")
    fun updateUser(
        @PathVariable("username") username: String,
        @RequestBody @Valid body: CreateOrUpdateUserDTO,
    ): ResponseEntity<DomainUser> =
        ResponseEntity.ofNullable(userService.updateUserInfo(username, body))

    @DeleteMapping("{username}")
    fun deleteUser(@PathVariable("username") username: String): ResponseEntity<Void> =
        userService.deleteUser(username).let { ResponseEntity.noContent().build() }
}

data class PageableResult<T>(val content: List<T>, val page: Int, val size: Int, val total: Long)

data class CreateOrUpdateUserDTO(
    val firstName: String?,
    val lastName: String?,
    val roles: Set<AuthorityConstants>?,
    @field:Email val email: String,
    @field:Size(min = 6) val username: String,
)
