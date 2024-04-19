package cl.ravenhill.pigeon.callbacks

import cl.ravenhill.pigeon.chat.ReadUser
import com.github.kotlintelegrambot.Bot
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Defines the interface for handling callback queries from inline keyboard interactions in a Telegram bot.
 * Implementations of this interface are responsible for processing user interactions that result from pressing
 * buttons on inline keyboards, which are commonly used in bots for quick responses and commands.
 *
 * @property name
 *  The unique name of the callback query handler, used primarily for callback data in Telegram's inline keyboards.
 */
sealed class CallbackQueryHandler {
    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    abstract val name: String

    /**
     * Handles the callback query interaction from the user. This function is invoked when the user interacts with
     * an inline keyboard button that triggers a callback query. The implementation should define the behavior
     * to be executed based on the user's interaction.
     *
     * @param user
     *  The user who initiated the callback query.
     * @param bot
     *  The bot instance used to send messages and perform actions in response to the callback query.
     */
    abstract operator fun invoke(user: ReadUser, bot: Bot)
}
