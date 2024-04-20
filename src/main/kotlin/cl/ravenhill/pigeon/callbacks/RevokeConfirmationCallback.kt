package cl.ravenhill.pigeon.callbacks

import cl.ravenhill.pigeon.BotFailure
import cl.ravenhill.pigeon.BotSuccess
import cl.ravenhill.pigeon.bot.Bot
import cl.ravenhill.pigeon.callbacks.RevokeConfirmationYes.name
import cl.ravenhill.pigeon.chat.ReadUser
import cl.ravenhill.pigeon.db.DatabaseService
import cl.ravenhill.pigeon.db.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Represents a specialized category of callback query handlers for managing user confirmations for
 * revocation actions within a Telegram bot. This ensures consistent handling of all possible revocation
 * confirmations by encapsulating them within derived classes of a sealed class.
 */
sealed class RevokeConfirmationCallback : CallbackQueryHandler()

/**
 * Handles affirmative responses for revocation confirmations within the Telegram bot system.
 *
 * @property name The unique name identifying this handler, used in mapping this handler to specific callback queries.
 */
data object RevokeConfirmationYes : RevokeConfirmationCallback() {
    override val name: String = this::class.simpleName!!

    override fun invoke(user: ReadUser, bot: Bot, dbService: DatabaseService) = transaction {
            Users.deleteWhere { id eq user.userId }
            logger.info("User ${user.username} has been revoked.")
            when (val result = bot.sendMessage(user, "Your registration has been revoked.")) {
                is BotFailure -> {
                    logger.error("Failed to send revocation message to user ${user.username}")
                    CallbackFailure(result.message)
                }
                is BotSuccess -> {
                    logger.info("User ${user.username} has been revoked.")
                    CallbackSuccess("Your registration has been revoked.")
                }
            }
        }
}


data object RevokeConfirmationNo : RevokeConfirmationCallback() {
    override val name: String = this::class.simpleName!!

    override fun invoke(user: ReadUser, bot: Bot, dbService: DatabaseService): CallbackResult {
        logger.info("User ${user.username} has chosen not to revoke.")
        return when (val result = bot.sendMessage(user, "Your registration has not been revoked.")) {
            is BotFailure -> {
                logger.error("Failed to send revocation rejection message to user ${user.username}")
                CallbackFailure(result.message)
            }
            is BotSuccess -> {
                logger.info("User ${user.username} has chosen not to revoke.")
                CallbackSuccess("Your registration has not been revoked.")
            }
        }
    }
}
