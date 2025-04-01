package com.example.users

import com.fasterxml.jackson.databind.ObjectMapper
import java.lang.annotation.Inherited
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Autowired lateinit var userRepository: UserRepository

    @Autowired lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        userRepository.deleteAllInBatch()
    }

    @Test
    @WithMockUser
    fun `list all user as non-admin`() {
        mockMvc.get("/api/users").andExpect { status { isForbidden() } }
    }

    @Test
    @WithMockAdmin
    fun `list all users with no query params`() {
        mockMvc.get("/api/users").andExpectAll {
            status { isOk() }
            jsonPath("$.page") { value(DEFAULT_PAGE_NUMBER) }
            jsonPath("$.size") { value(DEFAULT_PAGE_SIZE) }
            jsonPath("$.total") { value(0) }
            jsonPath("$.content.length()") { value(0) }
        }
    }

    @Test
    @WithMockAdmin
    fun `list all users given page and size`() {
        userRepository.saveAll(List(20) { DomainUser.randomUser() })
        mockMvc
            .get("/api/users") {
                queryParam("page", "2")
                queryParam("size", "5")
            }
            .andExpectAll {
                status { isOk() }
                jsonPath("$.page") { value(2) }
                jsonPath("$.size") { value(5) }
                jsonPath("$.total") { value(20) }
                jsonPath("$.content.length()") { value(5) }
            }
    }

    @ParameterizedTest
    @ValueSource(strings = ["id", "username", "email", "createdBy", "updatedBy"])
    @WithMockAdmin
    fun `list all users given sorting queries`(property: String) {
        userRepository.saveAll(List(20) { DomainUser.randomUser() })

        mockMvc
            .get("/api/users") {
                queryParam("sort", "desc")
                queryParam("by", property)
            }
            .andExpectAll {
                status { isOk() }
                jsonPath("$.content[*].$property") {
                    value(
                        SortedBy { o1: Comparable<Any>, o2: Comparable<Any> ->
                            -1 * o1.compareTo(o2)
                        }
                    )
                }
            }
    }

    @Test
    @WithMockAdmin
    fun `fetch user by username`() {
        userRepository.saveAndFlush(DomainUser.defaultTestUser())

        mockMvc.get("/api/users/$DEFAULT_TEST_USERNAME").andExpectAll {
            status { isOk() }
            jsonPath("$.password") { doesNotHaveJsonPath() }
            jsonPath("$.username") { value(DEFAULT_TEST_USERNAME) }
        }
    }

    @Test
    @WithMockAdmin
    fun `update user successfully`() {
        val userId = userRepository.saveAndFlush(DomainUser.defaultTestUser(disabled = false)).id

        mockMvc.put("/api/users/$DEFAULT_TEST_USERNAME") {
            contentType = MediaType.APPLICATION_JSON
            content =
                objectMapper.writeValueAsString(
                    UpdateUserDTO(
                        firstName = "robert",
                        lastName = "morgan",
                        email = "robert-morgan@example.com",
                        username = "robert_morgan",
                        roles = emptySet(),
                    )
                )
        }

        val user = userRepository.findById(requireNotNull(userId)).orElseThrow()
        assertThat(user.firstName).isEqualTo("robert")
        assertThat(user.lastName).isEqualTo("morgan")
        assertThat(user.email).isEqualTo("robert-morgan@example.com")
        assertThat(user.username).isEqualTo("robert_morgan")
        assertThat(user.roles).isEmpty()
    }
}

@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@WithMockUser(authorities = ["ADMIN"])
annotation class WithMockAdmin

fun DomainUser.Companion.randomUser(disabled: Boolean = false): DomainUser =
    DomainUser(
        username = RandomStringUtils.secure().nextAlphabetic(6),
        email = "%s@example.com".format(RandomStringUtils.secure().nextAlphabetic(7)),
        password = "{noop}$DEFAULT_TEST_PASSWORD",
        disabled = disabled,
        roles = emptySet(),
        firstName = RandomStringUtils.secure().nextAlphabetic(5),
        lastName = RandomStringUtils.secure().nextAlphabetic(7),
        activationKey = DEFAULT_ACTIVATION_KEY.takeIf { disabled },
        resetKey = null,
        resetDate = null,
    )

class SortedBy<T>(val comparator: Comparator<T>) : TypeSafeMatcher<List<T>>() {
    lateinit var list: List<T>

    override fun describeTo(p0: Description) {
        p0.appendText(this.list.sortedWith(comparator).toList().toString())
    }

    override fun matchesSafely(list: List<T>): Boolean =
        list
            .zipWithNext { a, b -> this.comparator.compare(a, b) <= 0 }
            .all { it }
            .also { this.list = list }
}
