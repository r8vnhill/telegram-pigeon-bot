package cl.ravenhill.pigeon.states

import cl.ravenhill.pigeon.BotFailure
import cl.ravenhill.pigeon.BotResult
import cl.ravenhill.pigeon.BotSuccess
import cl.ravenhill.pigeon.chat.ReadUser
import cl.ravenhill.pigeon.db.Users
import cl.ravenhill.pigeon.sendMessage
import com.github.kotlintelegrambot.Bot
import org.jetbrains.exposed.sql.selectAll
import org.slf4j.LoggerFactory


private val logger = LoggerFactory.getLogger("states")

/**
 * Verifies whether a user has been successfully deleted from the database after an operation that intended to remove
 * them. This function checks the result of the operation and confirms the absence of the user's record in the database.
 *
 * ## Usage:
 * This function is typically called after an attempt to delete a user from the database. It ensures that the deletion
 * was successful by verifying that no records exist for the user. If the user still exists, it returns a `BotFailure`
 * indicating the failure to delete the user.
 *
 * ### Example:
 * ```kotlin
 * val deletionResult = someDeletionFunction(user)
 * val verificationResult = verifyUserDeletion(deletionResult, user)
 * if (verificationResult is BotFailure) {
 *     println("Error: ${verificationResult.message}")
 * }
 * ```
 *
 * @param result The result of the deletion operation, which is checked to determine if further verification is needed.
 * @param user The `ReadUser` whose deletion is being verified. This user's ID is used to check for existing records.
 * @return
 *  Returns the original `BotResult` if the deletion was confirmed, or a `BotFailure` if the user was not successfully
 *  deleted.
 */
fun verifyUserDeletion(result: BotResult, user: ReadUser): BotResult {
    // Check if the operation was initially successful.
    if (result is BotSuccess) {
        val exists = Users.selectAll().where { Users.id eq user.userId }.count() > 0
        // If the user still exists in the database, return a failure result.
        if (exists) return BotFailure("User was not deleted")
    }
    // Return the original result if no issues were found.
    return result
}


/**
 * Verifies if the state of a user in the database matches an expected state after an operation. This function is
 * crucial for ensuring the integrity of state changes within user management workflows.
 *
 * ## Usage:
 * This function should be used after any operation that is supposed to alter a user's state in the database. It checks
 * whether the operation has successfully updated the user's state to the expected value.
 *
 * ### Example:
 * ```kotlin
 * val updateResult = updateUserState(user, "StartState")
 * val verifyResult = verifyUserState(updateResult, "StartState", user)
 * if (verifyResult is BotFailure) {
 *     println("Error: ${verifyResult.message}")
 * }
 * ```
 *
 * @param result
 *  The `BotResult` returned from the previous operation, which is checked to determine if the operation was initially
 *  deemed successful.
 * @param expectedState
 *  The state expected to be set for the user in the database.
 * @param user
 *  The `ReadUser` whose state is being verified. This user's ID is used to check the actual state in the database.
 * @return
 *  Returns the original `BotResult` if the user's state was correctly updated, or a `BotFailure` if the state does not
 *  match the expected value.
 */
fun verifyUserState(result: BotResult, expectedState: String, user: ReadUser): BotResult {
    // Proceed with verification only if the previous result was a success.
    if (result is BotSuccess) {
        // Check if the user's state in the database matches the expected state.
        val isCorrectState = Users.selectAll().where { Users.id eq user.userId }
            .single()[Users.state] == expectedState
        // Return a failure if the state was not updated as expected.
        if (!isCorrectState) return BotFailure("User state was not updated")
    }
    // Return the original result if the state was correctly updated, or if the initial operation was not a success.
    return result
}


fun handleInvalidInput(bot: Bot, context: ReadUser): BotResult {
    logger.warn("Invalid input from user ${context.username.ifBlank { context.userId.toString() }}")
    val message = "Invalid input. Please type 'yes' or 'no' to confirm or deny registration."
    return sendMessage(bot, message, context)
}
