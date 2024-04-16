package cl.ravenhill.pigeon.states

import cl.ravenhill.pigeon.BotResult
import cl.ravenhill.pigeon.BotSuccess
import cl.ravenhill.pigeon.chat.ReadWriteUser
import cl.ravenhill.pigeon.db.Users
import cl.ravenhill.pigeon.sendMessage
import com.github.kotlintelegrambot.Bot
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

data class RevokeState(override val context: ReadWriteUser) : State {
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        context.state = this
    }

    override fun process(text: String?, bot: Bot): BotResult {
        super.process(text, bot)
        val cleanText = text?.uppercase() ?: "INVALID"
        return when (cleanText) {
            "YES" -> handleConfirmation(bot)
            "NO" -> handleRejection(bot)
            else -> handleInvalidInput(bot, context)
        }
    }

    private fun handleConfirmation(bot: Bot): BotResult = transaction {
        Users.deleteWhere { id eq context.userId }
        logger.info("User ${context.username} has been revoked.")
        sendMessage(bot, "Your registration has been revoked.", context)
        BotSuccess("Your registration has been revoked.")
    }

    private fun handleRejection(bot: Bot): BotResult {
        logger.info("User ${context.username} has chosen not to revoke.")
        sendMessage(bot, "Your registration has not been revoked.", context)
        return BotSuccess("Your registration has not been revoked.")
    }
}