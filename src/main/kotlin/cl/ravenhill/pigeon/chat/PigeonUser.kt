package cl.ravenhill.pigeon.chat

import cl.ravenhill.jakt.Jakt.constraints
import cl.ravenhill.pigeon.BeNull
import cl.ravenhill.pigeon.db.Users
import cl.ravenhill.pigeon.states.IdleState
import cl.ravenhill.pigeon.states.StartState
import cl.ravenhill.pigeon.states.State
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

/**
 * Represents a user in the Pigeon Telegram bot system. This data class encapsulates essential user information,
 * specifically the username and user ID, and provides functionality to convert this into a Telegram bot API `User` object.
 *
 * ## Usage:
 * This class is primarily used to manage user information within the bot and to interact with the Telegram bot API,
 * where conversion of internal user representation to the API's user model is necessary.
 *
 * ### Example 1: Creating a PigeonUser instance
 * ```kotlin
 * val pigeonUser = PigeonUser("exampleUser", 1234567890L)
 * ```
 * ### Example 2: Converting a PigeonUser to a Telegram User
 * ```kotlin
 * val telegramUser = pigeonUser.toUser()
 * println(telegramUser)
 * ```
 *
 * @property username the username of the pigeon user.
 * @property userId the unique identifier of the pigeon user.
 */
data class PigeonUser(val username: String, val userId: Long) {
    var state: State = IdleState(this)

    /**
     * Converts a `PigeonUser` instance to a `User` object from the Telegram bot API. This method facilitates
     * the integration with Telegram's user management by providing a compatible user object.
     *
     * @return a `User` object containing the ID and username from this `PigeonUser`, with the isBot field set to false.
     */
    fun toUser() = User(userId, false, null.toString(), null, username, null, null, null)

    fun onStart(bot: Bot) {
        transaction {
            Users.insert {
                it[id] = this@PigeonUser.userId
                it[username] = this@PigeonUser.username
                it[state] = StartState::class.simpleName!!
            }
        }
        state.onStart(bot)
    }

    override fun toString() = "PigeonUser(username='$username', userId=$userId, state=$state)"

    fun onIdle() {
        transaction {
            Users.update({ Users.id eq userId }) {
                it[state] = IdleState::class.simpleName!!
            }
        }
    }

    companion object {
        fun from(from: User) = PigeonUser(from.username ?: "", from.id)
        fun from(row: ResultRow) = transaction {
            constraints {
                "User must have an ID" {
                    row.getOrNull(Users.id) mustNot BeNull
                }
                "User must have a username" {
                    row.getOrNull(Users.username) mustNot BeNull
                }
                "User must have a state" {
                    row.getOrNull(Users.state) mustNot BeNull
                }
            }
            val user = PigeonUser(row[Users.username], row[Users.chatId])
            user.state = when (row[Users.state]) {
                IdleState::class.simpleName -> IdleState(user)
                StartState::class.simpleName -> StartState(user)
                else -> throw IllegalArgumentException("Unknown state")
            }
            user
        }
    }
}
