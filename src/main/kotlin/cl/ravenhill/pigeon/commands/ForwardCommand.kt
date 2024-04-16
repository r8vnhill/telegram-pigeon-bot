package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.chat.ChatId
import cl.ravenhill.pigeon.chat.PigeonUser
import cl.ravenhill.pigeon.db.Admins
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Represents a forward command in the Telegram pigeon bot system. This class implements the Command interface,
 * providing functionality to forward a message to another user.
 *
 * @property message the message to be forwarded.
 * @property user the user who initiated the command.
 * @property parameters additional parameters associated with the command.
 */
data class ForwardCommand(
    val message: String,
    override val user: PigeonUser,
    override val parameters: List<String>
) : Command {
    override val name: String = "forward"
    override fun execute(): CommandResult {
        TODO("Not yet implemented")
    }

    override fun toString() =
        "ForwardCommand(message='$message', user=${user.username}, parameters=$parameters)"

    companion object
}

/**
 * Registers the "forward" command within the bot's Dispatcher. This function is an extension to the
 * Dispatcher context, enhancing it with the ability to handle the "forward" command. When this
 * command is received, the bot sends a message indicating that a message is being forwarded.
 *
 * ## Usage:
 * This function is intended to be called within the bot setup to handle the "forward" command. It
 * sends a message and then logs the outcome.
 *
 * ### Example: Integrating ForwardCommand in the bot's command dispatch
 * ```kotlin
 * fun main() = bot {
 *     token = java.io.File(".secret").readText()
 *     dispatch {
 *         ForwardCommand.register()
 *     }
 * }.startPolling()
 * ```
 *
 * In the example, `ForwardCommand.register()` is invoked within the `dispatch` block of the bot's setup,
 * effectively setting up the bot to listen and respond to the "forward" command during its operation.
 *
 * @receiver Dispatcher The `Dispatcher` that the "forward" command is registered to.
 */
context(Dispatcher)
fun ForwardCommand.Companion.register() = command("forward") {
    transaction {
        if (Admins.selectAll().where { Admins.chatId eq message.chat.id }.count() == 0L) {
            bot.sendMessage(ChatId.fromId(message.chat.id), "You are not authorized to use this command.")
        } else {
            bot.sendMessage(ChatId.fromId(message.chat.id), "Forwarding message...")
        }
    }
}
