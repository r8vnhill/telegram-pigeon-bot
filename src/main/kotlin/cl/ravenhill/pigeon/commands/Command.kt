package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.bot.Bot
import cl.ravenhill.pigeon.chat.ReadUser
import cl.ravenhill.pigeon.db.DatabaseService

/**
 * Represents a command in the Telegram pigeon bot system. This sealed interface establishes a common framework
 * for all command types, enabling specific implementations to define customized behavior and properties.
 * It encapsulates essential command elements including its name, the initiating user, parameters, associated bot,
 * and database service to be used.
 *
 * ## Usage:
 * Utilize this interface to define and manage different types of commands within the bot. Each implementation of
 * this interface can specify actions that the bot should perform in response to user commands, leveraging user input
 * and parameters.
 *
 * ### Example 1: Implementing a simple echo command
 * ```kotlin
 * class EchoCommand(
 *     override val name: String,
 *     override val user: ReadUser,
 *     override val parameters: List<String>,
 *     override val bot: Bot,
 *     override val databaseService: DatabaseService
 * ) : Command {
 *     override fun execute(): CommandResult {
 *         println("Echo Command: ${parameters.joinToString(" ")}")
 *         return CommandResult.Success(user, "Executed echo command successfully.")
 *     }
 * }
 * ```
 *
 * ### Example 2: Using the Command interface in a command handler
 * ```kotlin
 * fun handleCommand(command: Command) {
 *     val result = when (command) {
 *         is EchoCommand -> command.execute()
 *         else -> CommandResult.Failure(command.user, "Command not supported.")
 *     }
 *     println("Command result: ${result.message}")
 * }
 * ```
 *
 * @property name
 *  The name of the command, used for identifying and routing commands.
 * @property user
 *  The `ReadUser` who initiated the command, providing context about the sender.
 * @property parameters
 *  A list of strings representing the parameters or arguments provided with the command.
 * @property bot
 *  The `Bot` instance through which the command is processed, allowing for interactions like sending messages.
 * @property databaseService
 *  The `DatabaseService` instance used for any database operations required by the command.
 * @function execute
 *  The method to be implemented by each command, defining the logic to execute and returning a `CommandResult`.
 */
sealed interface Command {
    val user: ReadUser
    val parameters: List<String>
    val bot: Bot
    val databaseService: DatabaseService
    fun execute(): CommandResult
}

