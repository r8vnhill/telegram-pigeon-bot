package cl.ravenhill.pigeon.commands

import com.github.kotlintelegrambot.entities.User

sealed interface CommandResult {
    val user: User
    val message: String
}

data class Success(override val user: User, override val message: String) : CommandResult

data class Failure(override val user: User, override val message: String) : CommandResult
