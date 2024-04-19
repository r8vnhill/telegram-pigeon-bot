package cl.ravenhill.pigeon.callbacks

import cl.ravenhill.pigeon.chat.ReadUser
import cl.ravenhill.pigeon.db.Users
import cl.ravenhill.pigeon.sendMessage
import com.github.kotlintelegrambot.Bot
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

    override fun invoke(user: ReadUser, bot: Bot) {
        transaction() {
            Users.deleteWhere { id eq user.userId }
            logger.info("User ${user.username} has been revoked.")
            sendMessage(bot, "Your registration has been revoked.", user)
        }
    }
}


data object RevokeConfirmationNo : RevokeConfirmationCallback (){
    override val name: String = this::class.simpleName!!

    override fun invoke(user: ReadUser, bot: Bot) {
        logger.info("User ${user.username} has chosen not to revoke.")
        sendMessage(bot, "Your registration has not been revoked.", user)
    }
}
