package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.chat.ReadUser
import com.github.kotlintelegrambot.Bot


/**
 * Represents a Command in the Telegram pigeon bot system. This sealed interface ensures
 * that all command types share a common structure while allowing specific implementations
 * to define additional behavior and properties. It encapsulates the basic elements of a command
 * such as its name, the user who initiated it, and any parameters associated with the command.
 *
 * ## Usage:
 * This interface is used to define and handle different types of commands within the bot. It serves as a base
 * for creating commands that can perform various actions depending on the user input.
 *
 * ### Example 1: Implementing a simple command
 * ```kotlin
 * class EchoCommand(override val name: String, override val user: ChatId, override val parameters: List<String>) : Command {
 *     fun execute() {
 *         println("Echo Command: ${parameters.joinToString(" ")}")
 *     }
 * }
 * ```
 * ### Example 2: Using Command in a command handler
 * ```kotlin
 * fun handleCommand(command: Command) {
 *     when (command) {
 *         is EchoCommand -> command.execute()
 *         else -> println("Command not supported")
 *     }
 * }
 * ```
 * @property name the name of the command.
 * @property user the user ID associated with the command.
 * @property parameters a list of parameters for the command.
 */
sealed interface Command {
    val name: String
    val user: ReadUser
    val parameters: List<String>
    val bot: Bot
    fun execute(): CommandResult
}
