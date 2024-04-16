package cl.ravenhill.pigeon.chat

import cl.ravenhill.jakt.Jakt.constraints
import cl.ravenhill.pigeon.BeNull
import cl.ravenhill.pigeon.db.Users
import cl.ravenhill.pigeon.states.IdleState
import cl.ravenhill.pigeon.states.RevokeState
import cl.ravenhill.pigeon.states.StartState
import cl.ravenhill.pigeon.states.State
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.transactions.transaction
import com.github.kotlintelegrambot.entities.User as TelegramUser

/**
 * Represents a user in the Pigeon Telegram bot system. This data class encapsulates critical user information,
 * including the username and user ID. It supports converting this data into a Telegram bot API `User` object,
 * and handles interactions with the system's state management and database. The class conforms to the `ReadWriteUser`
 * interface, allowing dynamic state management based on user interactions.
 *
 * ## Usage:
 * Utilize this class to manage user data within the bot, seamlessly converting user data between the Telegram bot
 * representation and the system's internal format, and handling state transitions as users interact with the bot.
 *
 * ### Example: Creating and managing a PigeonUser
 * ```kotlin
 * val pigeonUser = PigeonUser("john_doe", 12345L)
 * println(pigeonUser)
 * pigeonUser.state = StartState(pigeonUser)
 * println("User is in state: ${pigeonUser.state}")
 * ```
 *
 * @property username The username of the Pigeon user, used as a unique identifier within the Telegram system.
 * @property userId The unique identifier of the Pigeon user, typically linked with their Telegram account ID.
 * @property state The current state of the Pigeon user, dynamically managed and updated during the user's session.
 */
data class PigeonUser(
    override val username: String,
    override val userId: Long
) : ReadWriteUser {
    override var state: State = IdleState(this)

    override fun toString() = "PigeonUser(username='$username', userId=$userId, state=$state)"

    companion object {
        /**
         * Factory method to create a `PigeonUser` from a Telegram API `User`.
         *
         * This method allows easy conversion from a Telegram user object to a `PigeonUser`,
         * facilitating the integration of Telegram data with internal systems.
         *
         * @param from The Telegram user object to convert.
         * @return A new instance of `PigeonUser` initialized with data from the Telegram user.
         */
        fun from(from: TelegramUser) = PigeonUser(from.username ?: "unknown", from.id)

        /**
         * Factory method to create a `PigeonUser` from a database row. This method ensures that all necessary data
         * is present in the row before attempting to create a user object, thereby avoiding partial or invalid data
         * setups.
         *
         * @param row The database row containing user data.
         * @return A new instance of `PigeonUser` configured with data from the database row.
         * @throws IllegalArgumentException if essential data fields are missing in the row.
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
                RevokeState::class.simpleName -> RevokeState(user)
                else -> throw IllegalArgumentException("Unknown state")
            }
            user
        }
    }
}
