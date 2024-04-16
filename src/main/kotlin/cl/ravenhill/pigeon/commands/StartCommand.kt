package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.chat.ReadUser
import cl.ravenhill.pigeon.db.Users
import cl.ravenhill.pigeon.callbacks.StartConfirmationNo
import cl.ravenhill.pigeon.callbacks.StartConfirmationYes
import cl.ravenhill.pigeon.states.IdleState
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
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
    override val bot: Bot
) : Command {
    private val logger = LoggerFactory.getLogger(javaClass)  // Logger for tracking command execution.

    override val name: String = "start"  // Command name.
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
        val result = transaction {
            if (Users.selectAll().where { Users.id eq user.userId }.count() == 0L) {
                val welcomeFile = File("messages/welcome_message.md")
                if (welcomeFile.exists() && user.state is IdleState) {
                    sendWelcomeMessage(welcomeFile)
                } else {
                    CommandFailure(user, "Welcome message not found")
                }
            } else {
                bot.sendMessage(ChatId.fromId(user.userId), "Welcome back!", parseMode = ParseMode.MARKDOWN)
                CommandSuccess(user, "User already exists in the database")
            }
        }
        logger.info("Start command result: $result")
        return result
    }

    private fun sendWelcomeMessage(welcomeFile: File): CommandSuccess {
        val inlineKeyboardMarkup = inlineKeyboardMarkup()
        bot.sendMessage(
            ChatId.fromId(user.userId),
            welcomeFile.readText(),
            parseMode = ParseMode.MARKDOWN,
            replyMarkup = inlineKeyboardMarkup
        )
        user.onStart(bot)
        return CommandSuccess(user, "User does not exist in the database, welcome message sent")
    }

    private fun inlineKeyboardMarkup() = InlineKeyboardMarkup.create(
        listOf(
            listOf(
                InlineKeyboardButton.CallbackData("Yes", StartConfirmationYes.name),
                InlineKeyboardButton.CallbackData("No", StartConfirmationNo.name)
            )
        )
    )
}
