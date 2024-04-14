package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.chat.PigeonUser
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId

/**
 * Represents a forward command in the Telegram pigeon bot system. This class implements the Command interface,
 * providing functionality to forward a message to another user.
 *
 * @property message the message to be forwarded.
 * @property user the user who initiated the command.
 * @property parameters additional parameters associated with the command.
 */
data class ForwardCommand(val message: String, override val user: PigeonUser, override val parameters: List<String>) :
    Command {
    override val name: String = "forward"

    /**
     * Executes the ForwardCommand.
     *
     * @return The result of executing the ForwardCommand, which may be a success ir failure.
     */
    override fun execute(): CommandResult =
        Success(user, "Message forwarded successfully")

    override fun toString() = "ForwardCommand(message=$message, user=${user.username}, parameters=$parameters)"

    companion object {
        fun Dispatcher.register() = command("forward") {
            val result = bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Forwarding message...")
            result.fold({
                println("[${it.chat.id}]: ${it.text}")
            }, {
                println("Error")
            })
        }
    }
}

