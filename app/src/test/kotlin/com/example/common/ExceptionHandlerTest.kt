package com.example.common

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class ExceptionHandlerTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Autowired lateinit var objectMapper: ObjectMapper

    @Test
    fun `should return 400 and bad fields given invalid request`() {
        mockMvc
            .post("/api/exception-handler-test/params") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(DummyBody("short"))
            }
            .andExpect { status { isBadRequest() } }
    }

    @Test
    fun `should return conflict given illegal state exception`() {
        mockMvc
            .post("/api/exception-handler-test/illegal-state") {
                contentType = MediaType.APPLICATION_JSON
            }
            .andExpect { status { isConflict() } }
    }
}

@RestController
@RequestMapping("/api/exception-handler-test")
class ExceptionHandlerController {

    @PostMapping("params")
    fun invalidParam(@RequestBody @Valid body: DummyBody): ResponseEntity<DummyBody> =
        ResponseEntity.ok(body)

    @PostMapping("illegal-state")
    fun illegalState(): ResponseEntity<Void> = error("something is wrong")
}

data class DummyBody(@field:Size(min = 10) val key: String)
