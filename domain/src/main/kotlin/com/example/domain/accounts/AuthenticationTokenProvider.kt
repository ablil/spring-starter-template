package com.example.domain.accounts

import java.util.function.Function

@FunctionalInterface interface AuthenticationTokenProvider : Function<UserAccount, Token>
