package com.example.common

import com.example.users.WithMockAdmin
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class ActuatorIT {

    @Autowired lateinit var mockMvc: MockMvc

    @Test
    fun `should access health endpoint given no credentials`() {
        mockMvc.get("/actuator/health").andExpect { status { isOk() } }
    }

    @Test
    @WithMockAdmin
    fun `should access info endpoint given an admin`() {
        mockMvc.get("/actuator/info").andExpectAll {
            status { isOk() }
            jsonPath("$.build.time") { exists() }
        }
    }

    @Test
    @WithMockAdmin
    fun `should access loggers endpoint given an admin user`() {
        mockMvc.get("/actuator/loggers").andExpect { status { isOk() } }
    }

    @Test
    @WithMockUser
    fun `should not access loggers endpoint given non-admin users`() {
        mockMvc.get("/actuator/loggers").andExpect { status { isForbidden() } }
    }
}
