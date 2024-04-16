package cl.ravenhill.pigeon.states

import cl.ravenhill.pigeon.BotFailure
import cl.ravenhill.pigeon.BotResult
import cl.ravenhill.pigeon.BotSuccess
import cl.ravenhill.pigeon.chat.ChatId
import cl.ravenhill.pigeon.chat.ReadWriteUser
import cl.ravenhill.pigeon.db.Users
import com.github.kotlintelegrambot.Bot
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

/**
 * Represents the state of a user actively engaging in the registration process within the Telegram bot system.
 * This state handles responses to registration prompts, managing transitions between states based on user input,
 * and updating or validating user records in the database.
 *
 * ## Usage:
 * This class is primarily used during the registration workflow to interpret user responses and guide them through
 * the necessary steps of registration. Depending on the user's input, it can confirm registration, reject it,
 * or request clarification in the case of unclear input.
 *
 * ### Example:
 * ```kotlin
 * val startState = StartState(user)
 * val result = startState.process("YES", bot)
 * println(result.message) // Expected to log a success or failure message based on user input.
 * ```
 *
 * @property context The context encapsulates the `ReadWriteUser` who is currently interacting with the bot.
 */
data class StartState(override val context: ReadWriteUser) : State {
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        context.state = this
    }

    /**
     * Processes the user's text input during the registration process and determines the next steps based
     * on the content of the input.
     *
     * @param text The user's input text, possibly null, indicating their decision during registration.
     * @param bot The Bot instance used to send messages to the user.
     * @return A `BotResult` indicating the outcome of the processing, including any state transitions or
     * necessary messages sent to the user.
     */
    override fun process(text: String?, bot: Bot): BotResult {
        super.process(text, bot) // This should be adjusted if super.process has meaningful behavior.
        val cleanText = text?.uppercase() ?: "INVALID"
        return when (cleanText) {
            "YES" -> handleConfirmation(bot)
            "NO" -> handleRejection(bot)
            else -> handleInvalidInput(bot)
        }
    }

    private fun handleConfirmation(bot: Bot): BotResult = transaction {
        logger.info("User ${context.username.ifBlank { context.userId.toString() }} confirmed start")
        val message = "You were successfully registered!"
        sendMessage(bot, message).also {
            context.onIdle(bot)
            verifyUserState(it, IdleState::class.simpleName!!)
        }
    }

    private fun handleRejection(bot: Bot): BotResult = transaction {
        logger.info("User ${context.username.ifBlank { context.userId.toString() }} denied start")
        val message = "You were not registered."
        sendMessage(bot, message).also {
            Users.deleteWhere { id eq context.userId }
            context.onIdle(bot)
            verifyUserDeletion(it)
        }
    }

    private fun handleInvalidInput(bot: Bot): BotResult = transaction {
        logger.warn("Invalid input from user ${context.username.ifBlank { context.userId.toString() }}")
        val message = "Invalid input. Please type 'yes' or 'no' to confirm or deny registration."
        sendMessage(bot, message)
    }

    private fun sendMessage(bot: Bot, message: String): BotResult =
        bot.sendMessage(ChatId.fromId(context.userId), message).fold(
            ifSuccess = {
                BotSuccess("Message sent to user ${context.username.ifBlank { context.userId.toString() }}: $message")
            },
            ifError = {
                BotFailure("Failed to send message to user ${context.username.ifBlank { context.userId.toString() }}")
            }
        )

    private fun verifyUserState(result: BotResult, expectedState: String): BotResult {
        if (result is BotSuccess) {
            val isCorrectState = Users.selectAll().where { Users.id eq context.userId }
                .single()[Users.state] == expectedState
            if (!isCorrectState) return BotFailure("User state was not updated")
        }
        return result
    }

    private fun verifyUserDeletion(result: BotResult): BotResult {
        if (result is BotSuccess) {
            val exists = Users.selectAll().where { Users.id eq context.userId }.count() > 0
            if (exists) return BotFailure("User was not deleted")
        }
        return result
    }
}
