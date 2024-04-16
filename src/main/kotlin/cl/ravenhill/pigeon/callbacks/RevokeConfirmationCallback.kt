package cl.ravenhill.pigeon.callbacks

import cl.ravenhill.pigeon.chat.ReadUser
import cl.ravenhill.pigeon.db.Users
import cl.ravenhill.pigeon.sendMessage
import com.github.kotlintelegrambot.Bot
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

sealed class RevokeConfirmationCallback : CallbackQueryHandler()

data object RevokeConfirmationYes : RevokeConfirmationCallback() {
    override val name: String = "yes_revoke"

    override fun invoke(user: ReadUser, bot: Bot) {
        transaction {
            Users.deleteWhere { id eq user.userId }
            logger.info("User ${user.username} has been revoked.")
            sendMessage(bot, "Your registration has been revoked.", user)
        }
    }
}

data object RevokeConfirmationNo : RevokeConfirmationCallback (){
    override val name: String = "no_revoke"

    override fun invoke(user: ReadUser, bot: Bot) {
        logger.info("User ${user.username} has chosen not to revoke.")
        sendMessage(bot, "Your registration has not been revoked.", user)
    }
}
