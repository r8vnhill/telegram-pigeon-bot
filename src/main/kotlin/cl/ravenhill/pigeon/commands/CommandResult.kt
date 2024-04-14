package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.chat.PigeonUser

/**
 * Defines a contract for the result of a command within the Telegram pigeon bot. This sealed interface
 * ensures that all command results share a common structure but differ in their implementation specifics,
 * enabling type-safe handling of different outcomes such as success or failure.
 *
 * ## Usage:
 * Use this sealed interface to define and handle different outcomes from commands processed by the bot.
 * It allows for exhaustive checking when handling command results, ensuring that all possible cases are considered.
 *
 * ### Example 1: Handling command results
 * ```kotlin
 * fun processCommandResult(commandResult: CommandResult) {
 *     when (commandResult) {
 *         is Success -> println("Success: ${commandResult.message}")
 *         is Failure -> println("Failure: ${commandResult.message}")
 *     }
 * }
 * ```
 * ### Example 2: Creating instances of command results
 * ```kotlin
 * val successResult = Success(user, "Operation completed successfully")
 * val failureResult = Failure(user, "Operation failed due to error")
 * ```
 * @property user the `User` who initiated the command, associated with this result.
 * @property message a descriptive message about the outcome of the command.
 */
sealed interface CommandResult {
    val user: PigeonUser
    val message: String
}

/**
 * Represents a successful outcome of a command execution. Inherits from `CommandResult`.
 *
 * @property user the `User` associated with this success result.
 * @property message a success message detailing the successful execution of the command.
 */
data class Success(override val user: PigeonUser, override val message: String) : CommandResult

/**
 * Represents a failure outcome of a command execution. Inherits from `CommandResult`.
 *
 * @property user the `User` associated with this failure result.
 * @property message a failure message detailing the issue encountered during the command execution.
 */
data class Failure(override val user: PigeonUser, override val message: String) : CommandResult
