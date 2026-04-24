package com.commercecore.backend.user.service

import com.commercecore.backend.user.api.v1.dto.CreateUserRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.UpdateUserRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.UserResponseV1Dto
import com.commercecore.backend.user.api.v1.mapper.UserMapperV1
import com.commercecore.backend.user.entity.User
import com.commercecore.backend.user.exception.UserConflictException
import com.commercecore.backend.user.exception.UserDeletedException
import com.commercecore.backend.user.exception.UserInactiveException
import com.commercecore.backend.user.exception.UserNotDeletedException
import com.commercecore.backend.user.exception.UserNotFoundException
import com.commercecore.backend.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override fun getAllUsers(): List<UserResponseV1Dto> {
        return userRepository.findAll().map(UserMapperV1::toResponseDto)
    }

    override fun getUserById(id: Long): UserResponseV1Dto {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException() }

        return UserMapperV1.toResponseDto(user)
    }

    override fun createUser(createUserRequestV1Dto: CreateUserRequestV1Dto): UserResponseV1Dto {
        if (userRepository.existsByEmail(createUserRequestV1Dto.email)) {
            throw UserConflictException()
        }

        val user = User(
            name = createUserRequestV1Dto.name,
            email = createUserRequestV1Dto.email,
            password = createUserRequestV1Dto.password
        )

        val savedUser = userRepository.save(user)
        return UserMapperV1.toResponseDto(savedUser)
    }

    override fun updateUser(id: Long, updateUserRequestV1Dto: UpdateUserRequestV1Dto): UserResponseV1Dto {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException() }

        val emailOwner = userRepository.findByEmail(updateUserRequestV1Dto.email)
        if (emailOwner.isPresent && emailOwner.get().id != id) {
            throw UserConflictException()
        }

        user.name = updateUserRequestV1Dto.name
        user.email = updateUserRequestV1Dto.email

        val updatedUser = userRepository.save(user)
        return UserMapperV1.toResponseDto(updatedUser)
    }

    override fun deactivateUser(id: Long): UserResponseV1Dto {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException() }

        if (!user.active) {
            throw UserInactiveException()
        }

        user.active = false

        val updatedUser = userRepository.save(user)
        return UserMapperV1.toResponseDto(updatedUser)
    }

    override fun activateUser(id: Long): UserResponseV1Dto {
        val user = userRepository.findById(id)
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

        user.deleted = false
        user.deletedAt = null

        val updatedUser = userRepository.save(user)
        return UserMapperV1.toResponseDto(updatedUser)
    }
}