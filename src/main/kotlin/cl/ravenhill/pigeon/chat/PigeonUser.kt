package cl.ravenhill.pigeon.chat

import cl.ravenhill.pigeon.states.IdleState
import cl.ravenhill.pigeon.states.State
import com.github.kotlintelegrambot.entities.User

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
 * @property id the unique identifier of the pigeon user.
 */
data class PigeonUser(val username: String, val id: Long) {
    var state: State = IdleState(this)

    /**
     * Converts a `PigeonUser` instance to a `User` object from the Telegram bot API. This method facilitates
     * the integration with Telegram's user management by providing a compatible user object.
     *
     * @return a `User` object containing the ID and username from this `PigeonUser`, with the isBot field set to false.
     */
    fun toUser() = User(id, false, null.toString(), null, username, null, null, null)

    companion object {
        fun from(from: User) = PigeonUser(from.username ?: "", from.id)
    }
}
