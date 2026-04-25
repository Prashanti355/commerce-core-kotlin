package com.commercecore.backend.user.service

import com.commercecore.backend.user.api.v1.dto.ChangePasswordRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.CreateUserRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.PatchUserRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.UpdateUserRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.UpdateUserRoleRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.UserResponseV1Dto
import com.commercecore.backend.user.api.v1.mapper.UserMapperV1
import com.commercecore.backend.user.entity.User
import com.commercecore.backend.user.exception.InvalidCurrentPasswordException
import com.commercecore.backend.user.exception.InvalidPatchRequestException
import com.commercecore.backend.user.exception.InvalidRoleException
import com.commercecore.backend.user.exception.UserConflictException
import com.commercecore.backend.user.exception.UserDeletedException
import com.commercecore.backend.user.exception.UserInactiveException
import com.commercecore.backend.user.exception.UserNotDeletedException
import com.commercecore.backend.user.exception.UserNotFoundException
import com.commercecore.backend.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import com.commercecore.backend.audit.service.AuditLogService

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val auditLogService: AuditLogService
) : UserService {

    override fun getAllUsers(): List<UserResponseV1Dto> {
        return userRepository.findAllByDeletedFalse().map(UserMapperV1::toResponseDto)
    }

    override fun getDeletedUsers(): List<UserResponseV1Dto> {
        return userRepository.findAllByDeletedTrue().map(UserMapperV1::toResponseDto)
    }

    override fun getUserById(id: Long): UserResponseV1Dto {
        val user = getNonDeletedUserOrThrow(id)
        return UserMapperV1.toResponseDto(user)
    }

    override fun createUser(createUserRequestV1Dto: CreateUserRequestV1Dto): UserResponseV1Dto {
        ensureEmailAvailable(createUserRequestV1Dto.email)

        val user = User(
            name = createUserRequestV1Dto.name,
            email = createUserRequestV1Dto.email,
            password = encodePassword(createUserRequestV1Dto.password)
        )

        val savedUser = userRepository.save(user)
        auditLogService.log(
            action = "USER_CREATE",
            entityType = "USER",
            entityId = savedUser.id,
            detail = "Usuario creado con email ${savedUser.email}"
        )
        return UserMapperV1.toResponseDto(savedUser)
    }

    override fun updateUser(id: Long, updateUserRequestV1Dto: UpdateUserRequestV1Dto): UserResponseV1Dto {
        val user = getNonDeletedUserOrThrow(id)

        ensureEmailAvailable(updateUserRequestV1Dto.email, id)

        user.name = updateUserRequestV1Dto.name
        user.email = updateUserRequestV1Dto.email

        val updatedUser = userRepository.save(user)
        auditLogService.log(
            action = "USER_UPDATE",
            entityType = "USER",
            entityId = updatedUser.id,
            detail = "Usuario actualizado con email ${updatedUser.email}"
        )
        return UserMapperV1.toResponseDto(updatedUser)
    }

    override fun patchUser(id: Long, patchUserRequestV1Dto: PatchUserRequestV1Dto): UserResponseV1Dto {
        val user = getNonDeletedUserOrThrow(id)

        if (patchUserRequestV1Dto.name == null && patchUserRequestV1Dto.email == null) {
            throw InvalidPatchRequestException()
        }

        patchUserRequestV1Dto.name?.let {
            user.name = it
        }

        patchUserRequestV1Dto.email?.let {
            ensureEmailAvailable(it, id)
            user.email = it
        }

        val updatedUser = userRepository.save(user)
        return UserMapperV1.toResponseDto(updatedUser)
    }

    override fun changePassword(id: Long, changePasswordRequestV1Dto: ChangePasswordRequestV1Dto): UserResponseV1Dto {
        val user = getNonDeletedUserOrThrow(id)

        if (!passwordEncoder.matches(changePasswordRequestV1Dto.currentPassword, user.password)) {
            throw InvalidCurrentPasswordException()
        }

        user.password = encodePassword(changePasswordRequestV1Dto.newPassword)

        val updatedUser = userRepository.save(user)
        auditLogService.log(
            action = "USER_CHANGE_PASSWORD",
            entityType = "USER",
            entityId = updatedUser.id,
            detail = "Contraseña actualizada"
        )
        return UserMapperV1.toResponseDto(updatedUser)
    }

    override fun updateUserRole(id: Long, updateUserRoleRequestV1Dto: UpdateUserRoleRequestV1Dto): UserResponseV1Dto {
        val user = getNonDeletedUserOrThrow(id)
        val normalizedRole = normalizeAndValidateRole(updateUserRoleRequestV1Dto.role)

        user.role = normalizedRole

        val updatedUser = userRepository.save(user)
        return UserMapperV1.toResponseDto(updatedUser)
    }

    override fun deactivateUser(id: Long): UserResponseV1Dto {
        val user = getNonDeletedUserOrThrow(id)

        if (!user.active) {
            throw UserInactiveException()
        }

        user.active = false

        val updatedUser = userRepository.save(user)
        return UserMapperV1.toResponseDto(updatedUser)
    }

    override fun activateUser(id: Long): UserResponseV1Dto {
        val user = getNonDeletedUserOrThrow(id)

        user.active = true

        val updatedUser = userRepository.save(user)
        return UserMapperV1.toResponseDto(updatedUser)
    }

    override fun deleteUser(id: Long): UserResponseV1Dto {
        val user = getUserOrThrow(id)

        if (user.deleted) {
            throw UserDeletedException()
        }

        user.deleted = true
        user.deletedAt = LocalDateTime.now()
        user.active = false

        val updatedUser = userRepository.save(user)
        auditLogService.log(
            action = "USER_DELETE",
            entityType = "USER",
            entityId = updatedUser.id,
            detail = "Usuario eliminado lógicamente"
        )
        return UserMapperV1.toResponseDto(updatedUser)
    }

    override fun restoreUser(id: Long): UserResponseV1Dto {
        val user = getUserOrThrow(id)

        if (!user.deleted) {
            throw UserNotDeletedException()
        }

        ensureEmailAvailable(user.email, id)

        user.deleted = false
        user.deletedAt = null
        user.active = false

        val updatedUser = userRepository.save(user)
        auditLogService.log(
            action = "USER_RESTORE",
            entityType = "USER",
            entityId = updatedUser.id,
            detail = "Usuario restaurado"
        )
        return UserMapperV1.toResponseDto(updatedUser)
    }

    private fun getUserOrThrow(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { UserNotFoundException() }
    }

    private fun getNonDeletedUserOrThrow(id: Long): User {
        val user = getUserOrThrow(id)

        if (user.deleted) {
            throw UserDeletedException("El usuario está eliminado. Restáuralo antes de operar sobre él")
        }

        return user
    }

    private fun ensureEmailAvailable(email: String, currentUserId: Long? = null) {
        val emailOwner = userRepository.findByEmailAndDeletedFalse(email)

        if (emailOwner.isPresent && emailOwner.get().id != currentUserId) {
            throw UserConflictException("Ya existe un usuario activo con ese correo")
        }
    }

    private fun normalizeAndValidateRole(role: String): String {
        val normalizedRole = role.trim().uppercase()

        return when (normalizedRole) {
            "ROLE_USER", "ROLE_ADMIN" -> normalizedRole
            else -> throw InvalidRoleException()
        }
    }

    private fun encodePassword(rawPassword: String): String {
        return passwordEncoder.encode(rawPassword)
            ?: throw IllegalStateException("No se pudo codificar la contraseña")
    }
}