package com.commercecore.backend.shared.exception

import com.commercecore.backend.shared.util.ResponseUtil
import com.commercecore.backend.user.exception.UserConflictException
import com.commercecore.backend.user.exception.UserInactiveException
import com.commercecore.backend.user.exception.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import com.commercecore.backend.user.exception.UserDeletedException
import com.commercecore.backend.user.exception.UserNotDeletedException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException) =
        ResponseUtil.error(HttpStatus.NOT_FOUND, ex.message ?: "Usuario no encontrado")

    @ExceptionHandler(UserConflictException::class)
    fun handleUserConflictException(ex: UserConflictException) =
        ResponseUtil.error(HttpStatus.CONFLICT, ex.message ?: "Conflicto de datos")

    @ExceptionHandler(UserInactiveException::class)
    fun handleUserInactiveException(ex: UserInactiveException) =
        ResponseUtil.error(HttpStatus.BAD_REQUEST, ex.message ?: "Usuario inactivo")

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException) =
        ResponseUtil.error(
            HttpStatus.BAD_REQUEST,
            ex.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        )

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParseException(ex: HttpMessageNotReadableException) =
        ResponseUtil.error(HttpStatus.BAD_REQUEST, "JSON inválido o mal formado")

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception) =
        ResponseUtil.error(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ex.message ?: "Error interno del servidor"
        )
    
    @ExceptionHandler(UserDeletedException::class)
    fun handleUserDeletedException(ex: UserDeletedException) =
        ResponseUtil.error(HttpStatus.BAD_REQUEST, ex.message ?: "Usuario ya eliminado")

    @ExceptionHandler(UserNotDeletedException::class)
    fun handleUserNotDeletedException(ex: UserNotDeletedException) =
        ResponseUtil.error(HttpStatus.BAD_REQUEST, ex.message ?: "Usuario no eliminado")    
}