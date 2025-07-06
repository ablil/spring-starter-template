package com.example.common

import com.example.users.DomainUser
import com.example.users.DEFAULT_CACHE
import com.example.users.UserRepository
import com.example.users.defaultTestUser
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.cache.set
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(properties = ["spring.cache.type=simple"])
class UserCacheEvictionAspectTest {
    @Autowired lateinit var cacheManager: CacheManager

    @Autowired lateinit var userRepository: UserRepository

    @Test
    fun `should clear users cache when user is persisted`() {
        val user = DomainUser.defaultTestUser(disabled = false)
        cacheManager.getCache(DEFAULT_CACHE)?.set(user.username, user)
        assertThat(cacheManager.getCache(DEFAULT_CACHE)?.get(user.username))
            .`as`("cache is set")
            .isNotNull()

        userRepository.saveAndFlush(user)
        assertThat(cacheManager.getCache(DEFAULT_CACHE)?.get(user.username))
            .`as`("cache is cleared")
            .isNull()
    }
}
