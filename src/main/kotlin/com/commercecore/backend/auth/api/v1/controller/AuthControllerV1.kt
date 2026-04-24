package com.commercecore.backend.auth.api.v1.controller

import com.commercecore.backend.auth.api.v1.dto.LoginRequestV1Dto
import com.commercecore.backend.auth.api.v1.dto.RefreshTokenRequestV1Dto
import com.commercecore.backend.auth.service.AuthService
import com.commercecore.backend.shared.util.ResponseUtil
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthControllerV1(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequestV1Dto: LoginRequestV1Dto) =
        ResponseUtil.success(
            message = "Inicio de sesión exitoso",
            data = authService.login(loginRequestV1Dto)
        )

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody refreshTokenRequestV1Dto: RefreshTokenRequestV1Dto) =
        ResponseUtil.success(
            message = "Token renovado correctamente",
            data = authService.refresh(refreshTokenRequestV1Dto)
        )

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") authorizationHeader: String) =
        ResponseUtil.success(
            message = "Sesión cerrada correctamente",
            data = run {
                authService.logout(authorizationHeader)
                mapOf("loggedOut" to true)
            }
        )

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    fun me() = ResponseUtil.success(
        message = "Usuario autenticado",
        data = authService.me(
            SecurityContextHolder.getContext().authentication?.name
                ?: throw IllegalStateException("No hay usuario autenticado")
        )
    )
}