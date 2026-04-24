package com.commercecore.backend.auth.service

import com.commercecore.backend.auth.api.v1.dto.AuthMeResponseV1Dto
import com.commercecore.backend.auth.api.v1.dto.AuthTokensResponseV1Dto
import com.commercecore.backend.auth.api.v1.dto.LoginRequestV1Dto
import com.commercecore.backend.auth.api.v1.dto.RefreshTokenRequestV1Dto

interface AuthService {

    fun login(loginRequestV1Dto: LoginRequestV1Dto): AuthTokensResponseV1Dto

    fun refresh(refreshTokenRequestV1Dto: RefreshTokenRequestV1Dto): AuthTokensResponseV1Dto

    fun logout(bearerToken: String)

    fun me(email: String): AuthMeResponseV1Dto
}