package com.example.common

import com.example.users.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class DomainUserDetailsService(val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails =
        userRepository
            .findByUsernameOrEmailIgnoreCase(username, username)
            ?.takeIf { !it.disabled }
            ?.let { createSpringSecurityUser(it) }
            ?: throw UsernameNotFoundException("user account $username NOT found OR disabled")

    private fun createSpringSecurityUser(domainUser: com.example.users.DomainUser): User =
        User(
            domainUser.username,
            domainUser.password,
            domainUser.roles.map { role -> SimpleGrantedAuthority(role.name) },
        )
}
