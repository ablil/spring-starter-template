package com.example.common

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "example.admin-management")
data class AdminManagementProperties(val username: String, val password: String)
