package cl.ravenhill.pigeon.callbacks

import cl.ravenhill.pigeon.chat.ChatId
import cl.ravenhill.pigeon.chat.ReadUser
import cl.ravenhill.pigeon.db.DatabaseService
import cl.ravenhill.pigeon.db.Users
import cl.ravenhill.pigeon.db.getUser
import cl.ravenhill.pigeon.states.IdleState
import com.github.kotlintelegrambot.Bot
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Defines a contract for handling start confirmation responses in the Telegram bot system. This sealed interface
 * ensures all start confirmation types are uniformly handled with a structured approach, facilitating different
 * behaviors based on user confirmation responses.
 *
 * ## Usage:
 * Implementations of this interface are used to handle specific responses to start confirmations, such as confirming
 * or denying a registration process via callback commands triggered from inline keyboards.
 *
 * @property name The unique name of the confirmation, used primarily for callback data in Telegram's inline keyboards.
 */
sealed class StartConfirmation : CallbackQueryHandler()

/**
 * Handles the affirmative response for a user starting interactions with the bot. If the user is not registered,
 * this object registers them and sends a welcoming message. If already registered, it informs them accordingly.
 */
data object StartConfirmationYes : StartConfirmation() {
    override val name: String = this::class.simpleName!!

    override fun invoke(user: ReadUser, bot: Bot, dbService: DatabaseService) {
        transaction {
            val queryResult = dbService.getUser(user)
            if (queryResult == null) {
                Users.insert {
                    it[id] = user.userId
                    it[username] = user.username
                    it[state] = IdleState::class.simpleName!!
                }
                bot.sendMessage(ChatId.fromId(user.userId), "Welcome to the bot!")
                logger.info("User ${user.username.ifBlank { user.userId.toString() }} registered successfully")
            } else {
                logger.info("User ${user.username.ifBlank { user.userId.toString() }} is already registered")
                bot.sendMessage(ChatId.fromId(user.userId), "You are already registered!")
            }
        }
    }
}

/**
 * Handles the negative response for a user starting interactions with the bot. This object logs the user's decision
 * not to register and sends a message encouraging them that they can register later if they choose.
 */
data object StartConfirmationNo : StartConfirmation() {
    override val name: String = "no_start"
    override fun invoke(user: ReadUser, bot: Bot, dbService: DatabaseService) {
        logger.info("User ${user.username.ifBlank { user.userId.toString() }} chose not to register")
        bot.sendMessage(
            ChatId.fromId(user.userId), "You have chosen not to register. Remember you can always register later!"
        )
    }
}
