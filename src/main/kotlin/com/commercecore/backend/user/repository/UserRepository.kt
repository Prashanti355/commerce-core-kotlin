package com.commercecore.backend.user.repository

import com.commercecore.backend.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {

    fun findByEmailAndDeletedFalse(email: String): Optional<User>

    fun existsByEmailAndDeletedFalse(email: String): Boolean

    fun findAllByDeletedFalse(): List<User>

    fun findAllByDeletedTrue(): List<User>

    fun findByIdAndDeletedFalse(id: Long): Optional<User>
}