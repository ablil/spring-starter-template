package com.example.common

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
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
}
