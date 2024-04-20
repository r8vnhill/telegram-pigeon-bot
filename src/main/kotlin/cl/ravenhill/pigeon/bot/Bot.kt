package cl.ravenhill.pigeon.bot

import cl.ravenhill.pigeon.BotResult
import cl.ravenhill.pigeon.chat.ReadUser
import com.github.kotlintelegrambot.entities.ReplyMarkup

/**
 * Represents the interface for a bot capable of sending messages. This interface is essential for defining
 * the communication capabilities of bots, particularly in how they interact with users through messages.
 */
interface Bot {

    /**
     * Sends a message to a specified user. This function encapsulates the communication process between the bot
     * and the user, including optional interactive elements through reply markup. It is designed to be used within
     * bot implementations that require direct user interaction.
     *
     * @param user
     *  The `ReadUser` to whom the message will be sent. This parameter includes user identification details necessary
     *  for routing the message correctly.
     * @param message
     *  The content of the message to be sent to the user. It should be a plain text string, but formatting options
     *  might be available depending on the bot platform's capabilities.
     * @param replyMarkup
     *  Optional. A `ReplyMarkup` instance that can include interactive elements like custom keyboards or inline
     *  buttons, allowing the user to respond to the message directly.
     * @return
     *  A `BotResult` indicating the success or failure of the message delivery. This result can provide additional
     *  details about the operation's outcome, useful for error handling and user feedback.
     */
    fun sendMessage(user: ReadUser, message: String, replyMarkup: ReplyMarkup? = null): BotResult
}
