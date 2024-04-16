package cl.ravenhill.pigeon

/**
 * Represents the outcome of an operation within the Telegram bot system. This sealed interface is used as a base
 * type for representing different results of bot actions, such as sending messages or processing commands.
 * Being a sealed interface, all possible outcomes must be direct implementations of `BotResult`, which enables
 * exhaustive when-expression evaluation in Kotlin.
 *
 * ## Usage:
 * Use this interface to encapsulate the results of various bot operations, allowing for pattern matching and
 * type-safe handling of different outcomes (success or failure).
 *
 * ### Example:
 * ```kotlin
 * fun handleBotOperation(result: BotResult) {
 *     when (result) {
 *         is BotSuccess -> println("Success: ${result.message}")
 *         is BotFailure -> println("Failure: ${result.message}")
 *     }
 * }
 * ```
 */
sealed interface BotResult

/**
 * Represents a successful outcome of a bot operation. It holds a descriptive message about the success,
 * which can include details such as what action was successfully completed.
 *
 * @property message The success message providing more details about what was successfully executed.
 */
data class BotSuccess(val message: String) : BotResult

/**
 * Represents a failure outcome of a bot operation. It includes a message that describes the failure,
 * which can be useful for logging errors or informing users about the issue encountered.
 *
 * @property message The error message detailing the nature of the failure.
 */
data class BotFailure(val message: String) : BotResult

