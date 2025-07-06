package com.example.common

import com.example.users.DEFAULT_CACHE
import com.example.users.DomainUser
import com.example.users.getLogger
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component

@Aspect
@Component
class UserCacheEvictionAspect(val cacheManager: CacheManager) {

    val logger = getLogger()

    @Pointcut("execution(* com.example.*.UserRepository*.saveAndFlush(..))")
    fun anyUserPersistence() {
        // do nothing
    }

    @AfterReturning("anyUserPersistence()", returning = "retValue")
    fun clearCache(retValue: Any) {
        if (retValue !is DomainUser) {
            logger.warn("advice called with invalid return value {}", retValue)
            return
        }
        cacheManager.getCache(DEFAULT_CACHE)?.evictIfPresent(retValue.username)
    }
}
