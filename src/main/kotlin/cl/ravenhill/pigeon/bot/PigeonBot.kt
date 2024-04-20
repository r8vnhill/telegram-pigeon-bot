package cl.ravenhill.pigeon.bot

import cl.ravenhill.pigeon.BotFailure
import cl.ravenhill.pigeon.BotSuccess
import cl.ravenhill.pigeon.chat.ReadUser
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.Bot as TelegramBot


/**
 * Represents a specialized implementation of a bot in the Telegram pigeon bot system. This class extends the `Bot`
 * interface to implement the sendMessage functionality using a specific instance of `TelegramBot`.
 * It handles sending messages to users with optional reply markup and default formatting.
 *
 * @param bot
 *  The `TelegramBot` instance that this `PigeonBot` class uses to interact with the Telegram API. It encapsulates all
 *  the API calls to Telegram, managing the sending of messages and other interactions.
 */
class PigeonBot(private val bot: TelegramBot) : Bot {
    /**
     * Sends a message to a specified user with optional formatting and interactive elements.
     *
     * @param user
     *  The `ReadUser` who will receive the message. This user's ID is used to target the message correctly.
     * @param message
     *  The content of the message to be sent. This is formatted in Markdown by default.
     * @param replyMarkup
     *  Optional. An instance of `ReplyMarkup` to include interactive elements like keyboards with the message.
     * @return
     *  A `BotResult` indicating the outcome of the operation. Returns `BotSuccess` with a success message or
     *  `BotFailure` with an error message, depending on the result of the send operation.
     */
    override fun sendMessage(user: ReadUser, message: String, replyMarkup: ReplyMarkup?) =
        bot.sendMessage(ChatId.fromId(user.userId), message, ParseMode.MARKDOWN, replyMarkup = replyMarkup).fold(
            ifSuccess = {
                BotSuccess("Message sent to user ${user.username.ifBlank { user.userId.toString() }}: $message")
            },
            ifError = {
                BotFailure("Failed to send message to user ${user.username.ifBlank { user.userId.toString() }}")
            }
        )
}