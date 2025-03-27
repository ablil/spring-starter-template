package com.example.common

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
    fun `actuator health endpoint is accessible`() {
        mockMvc.get("/actuator/health").andExpect { status { isOk() } }
    }

    @Test
    @WithMockUser(authorities = ["ADMIN"])
    fun `loggers endpoint is accessible given an admin role`() {
        mockMvc.get("/actuator/loggers").andExpect { status { isOk() } }
    }

    @Test
    @WithMockUser
    fun `loggers endpoint is NOT accessible given non-admin user`() {
        mockMvc.get("/actuator/loggers").andExpect { status { isForbidden() } }
    }
}
