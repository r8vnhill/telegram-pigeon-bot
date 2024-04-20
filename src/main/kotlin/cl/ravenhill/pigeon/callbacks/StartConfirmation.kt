package cl.ravenhill.pigeon.callbacks

import cl.ravenhill.pigeon.BotFailure
import cl.ravenhill.pigeon.BotSuccess
import cl.ravenhill.pigeon.bot.Bot
import cl.ravenhill.pigeon.chat.ReadUser
import cl.ravenhill.pigeon.db.DatabaseService
import cl.ravenhill.pigeon.db.addUser
import cl.ravenhill.pigeon.db.getUser

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

    override fun invoke(user: ReadUser, bot: Bot, dbService: DatabaseService): CallbackResult {
        val readUser = dbService.getUser(user)
        if (readUser == null) {
            logger.info("User ${user.username.ifBlank { user.userId.toString() }} is not registered")
            dbService.addUser(user)
            return when (
                val result = bot.sendMessage(user, "Welcome to the bot!")
            ) {
                is BotFailure -> {
                    logger.error("Failed to send welcome message to user ${user.username}")
                    CallbackFailure(result.message)
                }
                is BotSuccess -> {
                    logger.info("User ${user.username.ifBlank { user.userId.toString() }} registered successfully")
                    CallbackSuccess("User registered successfully")
                }
            }
        } else {
            logger.info("User ${user.username.ifBlank { user.userId.toString() }} is already registered")
            return when (
                val result = bot.sendMessage(user, "You are already registered!")
            ) {
                is BotFailure -> {
                    logger.error("Failed to send message to user ${user.username}")
                    CallbackFailure(result.message)
                }
                is BotSuccess -> {
                    logger.info("User ${user.username.ifBlank { user.userId.toString() }} is already registered")
                    CallbackSuccess("User is already registered")
                }
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
    override fun invoke(user: ReadUser, bot: Bot, dbService: DatabaseService): CallbackResult {
        logger.info("User ${user.username.ifBlank { user.userId.toString() }} chose not to register")
        return when (
            val result = bot.sendMessage(
                user,
                "You have chosen not to register. Remember you can always register later!"
            )
        ) {
            is BotFailure -> {
                logger.error("Failed to send message to user ${user.username}")
                CallbackFailure(result.message)
            }

            is BotSuccess -> {
                logger.info("User ${user.username.ifBlank { user.userId.toString() }} chose not to register")
                CallbackSuccess("User chose not to register")
            }
        }
    }
}
