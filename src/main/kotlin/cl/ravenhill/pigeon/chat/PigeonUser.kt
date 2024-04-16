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
 * such as the username and user ID. It provides functionality to convert this into a Telegram bot API `User` object,
 * and to interact with the system's state management and database interactions.
 *
 * ## Usage:
 * This class is used to manage user information within the bot, convert user data between different representations,
 * and to facilitate user state management.
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
 * ### Example 3: Handling user state transition on bot start
 * ```kotlin
 * pigeonUser.onStart(bot)
 * ```
 *
 * @property username The username of the pigeon user.
 * @property userId The unique identifier of the pigeon user.
 * @property state The current state of the pigeon user, managed by state pattern.
 */
data class PigeonUser(val username: String, val userId: Long) {
    var state: State = IdleState(this)

    /**
     * Converts this `PigeonUser` instance to a `User` object from the Telegram bot API. This method facilitates
     * integration with Telegram's user management by providing a compatible user object, setting the `isBot` field to false.
     *
     * @return A `User` object containing the ID and username from this `PigeonUser`.
     */
    fun toUser() = User(userId, false, null.toString(), null, username, null, null, null)

    /**
     * Handles the initial start action for a user, which can include setting initial state and database operations.
     * This should be called when the bot starts interaction with a user.
     *
     * @param bot The active bot instance to interact with.
     */
    fun onStart(bot: Bot) {
        transaction {
            // Temporarily creating the user in the database
            Users.insert {
                it[id] = this@PigeonUser.userId
                it[username] = this@PigeonUser.username
                it[state] = StartState::class.simpleName!!
            }
        }
        state.onStart(bot)
    }

    override fun toString() = "PigeonUser(username='$username', userId=$userId, state=$state)"

    /**
     * Transitions the user to an idle state and updates the corresponding database record.
     */
    fun onIdle() {
        transaction {
            Users.update({ Users.id eq userId }) {
                it[state] = IdleState::class.simpleName!!
            }
        }
    }

    companion object {
        /**
         * Factory method to create a `PigeonUser` from a Telegram API `User`.
         *
         * @param from The Telegram user object.
         * @return A new instance of `PigeonUser`.
         */
        fun from(from: User) = PigeonUser(from.username ?: "", from.id)

        /**
         * Factory method to create a `PigeonUser` from a database row.
         *
         * @param row The database row containing user data.
         * @return A new instance of `PigeonUser` configured with data from the database row.
         */
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
