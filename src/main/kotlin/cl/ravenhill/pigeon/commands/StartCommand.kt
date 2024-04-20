package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.BotFailure
import cl.ravenhill.pigeon.BotSuccess
import cl.ravenhill.pigeon.bot.Bot
import cl.ravenhill.pigeon.chat.ReadUser
import cl.ravenhill.pigeon.callbacks.StartConfirmationNo
import cl.ravenhill.pigeon.callbacks.StartConfirmationYes
import cl.ravenhill.pigeon.db.DatabaseService
import cl.ravenhill.pigeon.db.getUser
import cl.ravenhill.pigeon.states.IdleState
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import org.slf4j.LoggerFactory
import java.io.File


/**
 * Represents the "start" command in the Telegram pigeon bot system, initiated by a user.
 * This command checks if the user is new or returning and sends the appropriate welcome message.
 * It also logs the activity and handles user registration or acknowledgment based on their existence in the database.
 *
 * ## Usage:
 * This command is typically bound to the initial interaction of a user with the bot, often triggered by
 * sending "/start" in the chat. It can also be used to reinitialize user data or provide information to new users.
 *
 * ### Example: Executing the start command
 * ```kotlin
 * val command = StartCommand(pigeonUser, bot)
 * val result = command.execute()
 * println(result)
 * ```
 *
 * @property user The `PigeonUser` who initiated the command.
 * @property bot The instance of `Bot` handling the Telegram interactions.
 */
data class StartCommand(
    override val user: ReadUser,
    override val bot: Bot,
    override val databaseService: DatabaseService
) : Command {
    private val logger = LoggerFactory.getLogger(javaClass)  // Logger for tracking command execution.

    override val parameters: List<String> = listOf()  // Parameters received by the command.

    /**
     * Executes the "start" command functionality. This method determines whether the user is new or returning
     * and sends an appropriate message. It logs the user's action and manages their state in the database.
     *
     * @return `CommandResult` indicating the outcome of the command execution. This could be a success with
     * a welcome message or a failure if the welcome message file is missing.
     */
    override fun execute(): CommandResult {
        logger.info("User ${user.username.ifBlank { user.userId.toString() }} started the bot")
        val result = if (databaseService.getUser(user) != null) {
            when (bot.sendMessage(user, "Welcome back!")) {
                is BotFailure -> CommandFailure(user, "Failed to send welcome back message")
                is BotSuccess -> CommandSuccess(user, "User already exists in the database")
            }
        } else {
            val welcomeFile = File("messages/welcome_message.md")
            if (welcomeFile.exists()) {
                sendWelcomeMessage(welcomeFile)
            } else {
                CommandFailure(user, "Welcome message not found")
            }
        }
        logger.info("Start command result: $result")
        return result
    }

    private fun sendWelcomeMessage(welcomeFile: File): CommandSuccess {
        val inlineKeyboardMarkup = inlineKeyboardMarkup()
        bot.sendMessage(user, welcomeFile.readText(), replyMarkup = inlineKeyboardMarkup)
        user.onStart(bot)
        return CommandSuccess(user, "User does not exist in the database, welcome message sent")
    }

    private fun inlineKeyboardMarkup(): ReplyMarkup {
        val yesButton = InlineKeyboardButton.CallbackData("Yes", StartConfirmationYes.name)
        val noButton = InlineKeyboardButton.CallbackData("No", StartConfirmationNo.name)
        val row = listOf(yesButton, noButton)
        return InlineKeyboardMarkup.createSingleRowKeyboard(row)
    }

    companion object {
        const val NAME: String = "start"
    }
}
