package com.commercecore.backend.user.service

import com.commercecore.backend.user.api.v1.dto.ChangePasswordRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.CreateUserRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.UpdateUserRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.UserResponseV1Dto
import com.commercecore.backend.user.api.v1.mapper.UserMapperV1
import com.commercecore.backend.user.entity.User
import com.commercecore.backend.user.exception.InvalidCurrentPasswordException
import com.commercecore.backend.user.exception.UserConflictException
import com.commercecore.backend.user.exception.UserDeletedException
import com.commercecore.backend.user.exception.UserInactiveException
import com.commercecore.backend.user.exception.UserNotDeletedException
import com.commercecore.backend.user.exception.UserNotFoundException
import com.commercecore.backend.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    override fun getAllUsers(): List<UserResponseV1Dto> {
        return userRepository.findAllByDeletedFalse().map(UserMapperV1::toResponseDto)
    }

    override fun getDeletedUsers(): List<UserResponseV1Dto> {
        return userRepository.findAllByDeletedTrue().map(UserMapperV1::toResponseDto)
    }

    override fun getUserById(id: Long): UserResponseV1Dto {
        val user = userRepository.findByIdAndDeletedFalse(id)
            .orElseThrow { UserNotFoundException() }

        return UserMapperV1.toResponseDto(user)
    }

    override fun createUser(createUserRequestV1Dto: CreateUserRequestV1Dto): UserResponseV1Dto {
        if (userRepository.existsByEmailAndDeletedFalse(createUserRequestV1Dto.email)) {
            throw UserConflictException()
        }

        val user = User(
            name = createUserRequestV1Dto.name,
            email = createUserRequestV1Dto.email,
            password = encodePassword(createUserRequestV1Dto.password)
        )

        val savedUser = userRepository.save(user)
        return UserMapperV1.toResponseDto(savedUser)
    }

    override fun updateUser(id: Long, updateUserRequestV1Dto: UpdateUserRequestV1Dto): UserResponseV1Dto {
        val user = userRepository.findByIdAndDeletedFalse(id)
            .orElseThrow { UserNotFoundException() }

        val emailOwner = userRepository.findByEmailAndDeletedFalse(updateUserRequestV1Dto.email)
        if (emailOwner.isPresent && emailOwner.get().id != id) {
            throw UserConflictException()
        }

        user.name = updateUserRequestV1Dto.name
        user.email = updateUserRequestV1Dto.email

        val updatedUser = userRepository.save(user)
        return UserMapperV1.toResponseDto(updatedUser)
    }

    override fun changePassword(id: Long, changePasswordRequestV1Dto: ChangePasswordRequestV1Dto): UserResponseV1Dto {
        val user = userRepository.findByIdAndDeletedFalse(id)
            .orElseThrow { UserNotFoundException() }

        if (!passwordEncoder.matches(changePasswordRequestV1Dto.currentPassword, user.password)) {
            throw InvalidCurrentPasswordException()
        }

        user.password = encodePassword(changePasswordRequestV1Dto.newPassword)

        val updatedUser = userRepository.save(user)
        return UserMapperV1.toResponseDto(updatedUser)
    }

    override fun deactivateUser(id: Long): UserResponseV1Dto {
        val user = userRepository.findByIdAndDeletedFalse(id)
            .orElseThrow { UserNotFoundException() }

        if (!user.active) {
            throw UserInactiveException()
        }

        user.active = false

        val updatedUser = userRepository.save(user)
        return UserMapperV1.toResponseDto(updatedUser)
    }

    override fun activateUser(id: Long): UserResponseV1Dto {
        val user = userRepository.findByIdAndDeletedFalse(id)
            .orElseThrow { UserNotFoundException() }

        user.active = true

        val updatedUser = userRepository.save(user)
        return UserMapperV1.toResponseDto(updatedUser)
    }

    override fun deleteUser(id: Long): UserResponseV1Dto {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException() }

        if (user.deleted) {
            throw UserDeletedException()
        }

        user.deleted = true
        user.deletedAt = LocalDateTime.now()

        val updatedUser = userRepository.save(user)
        return UserMapperV1.toResponseDto(updatedUser)
    }

    override fun restoreUser(id: Long): UserResponseV1Dto {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException() }

        if (!user.deleted) {
            throw UserNotDeletedException()
        }

        val emailOwner = userRepository.findByEmailAndDeletedFalse(user.email)
        if (emailOwner.isPresent && emailOwner.get().id != id) {
            throw UserConflictException("No se puede restaurar: ya existe un usuario activo con ese correo")
        }

        user.deleted = false
        user.deletedAt = null

        val updatedUser = userRepository.save(user)
        return UserMapperV1.toResponseDto(updatedUser)
    }

    // Convierte el resultado del encoder a no nulo de forma segura
    private fun encodePassword(rawPassword: String): String {
        return passwordEncoder.encode(rawPassword)
            ?: throw IllegalStateException("No se pudo codificar la contraseña")
    }
}