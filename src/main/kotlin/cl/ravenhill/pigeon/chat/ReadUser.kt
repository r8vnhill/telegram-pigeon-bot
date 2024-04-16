package cl.ravenhill.pigeon.chat

import cl.ravenhill.pigeon.db.Users
import cl.ravenhill.pigeon.states.IdleState
import cl.ravenhill.pigeon.states.StartState
import cl.ravenhill.pigeon.states.State
import cl.ravenhill.pigeon.states.TransitionResult
import com.github.kotlintelegrambot.Bot
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import com.github.kotlintelegrambot.entities.User as TelegramUser


/**
 * Interface for read-only users interacting with the Telegram bot, detailing the functionalities required for
 * transforming user data into Telegram-specific formats and handling state transitions. This interface is
 * particularly designed for users who have read access without the capabilities to modify core properties of
 * the user data. It ensures consistent and secure user interactions within the bot framework.
 *
 * ## Usage:
 * Implement this interface in classes representing users who interact with the Telegram bot in a non-intrusive manner.
 * It facilitates a uniform approach to managing user interactions, ensuring that all user actions adhere to
 * predetermined protocols for starting and idling interactions without altering underlying user data.
 *
 * @property username A unique identifier used within the system to recognize the user.
 * @property userId A numeric ID uniquely identifying the user, serving as a primary key in database transactions.
 * @property state Represents the current state of the user, dictating available interactions and behavior patterns.
 */
interface ReadUser {
    val username: String
    val userId: Long
    val state: State

    /**
     * Converts the internal representation of this user to a `TelegramUser`, ensuring compatibility with
     * the Telegram API. This method simplifies the integration of system-specific user models with the
     * external Telegram framework.
     *
     * @return
     *  `TelegramUser` - This return object represents the user formatted specifically for Telegram interactions,
     *  allowing for seamless communication and data handling within the Telegram ecosystem.
     */
    fun toTelegramUser(): TelegramUser = TelegramUser(userId, false, null.toString(), null, username)

    /**
     * Initiates the user's interaction with the bot. This method is critical for setting up the user's initial state
     * and ensuring that necessary database entries are created or updated. It is typically invoked when the user
     * first interacts with the bot, either through a command or a scheduled start.
     *
     * @param bot The bot instance currently managing user interactions.
     * @return
     *  `TransitionResult` - The result of the start action, indicating success or failure of the state transition.
     */
    fun onStart(bot: Bot): TransitionResult {
        transaction {
            Users.insert {
                it[id] = userId
                it[username] = this@ReadUser.username
                it[state] = StartState::class.simpleName!!
            }
        }
        return state.onStart(bot)
    }

    /**
     * Manages the transition of this user to an idle state. This method is called when the user's interaction is
     * minimized or deemed inactive, requiring updates to their record in the database to reflect this new state.
     *
     * @param bot The bot instance currently managing user interactions.
     * @return
     *  `TransitionResult` - The outcome of the idle transition, typically indicating the successful update of the
     *  user's state.
     */
    fun onIdle(bot: Bot): TransitionResult {
        transaction {
            Users.update({ Users.id eq userId }) {
                it[state] = IdleState::class.simpleName!!
            }
        }
        return state.onIdle(bot)
    }
}
