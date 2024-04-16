package cl.ravenhill.pigeon.states

import cl.ravenhill.pigeon.chat.ChatId
import cl.ravenhill.pigeon.chat.PigeonUser
import cl.ravenhill.pigeon.chat.ReadWriteUser
import cl.ravenhill.pigeon.db.Users
import com.github.kotlintelegrambot.Bot
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

data class StartState(override val context: ReadWriteUser) : State {
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        context.state = this
    }

    override fun process(text: String?, bot: Bot) {
        super.process(text, bot)
        when (text!!.uppercase()) {
            "YES" -> transaction {
                logger.info("User ${context.username.ifBlank { context.userId }} confirmed start")
                bot.sendMessage(ChatId.fromId(context.userId), "You were successfully registered!")
                context.onIdle(bot)
            }

            "NO" -> transaction {
                logger.info("User ${context.username.ifBlank { context.userId }} denied start")
                bot.sendMessage(ChatId.fromId(context.userId), "You were not registered.")
                Users.deleteWhere { id eq context.userId }
            }

            else -> transaction {
                logger.warn("User ${context.username.ifBlank { context.userId }} tried to start with invalid input")
                bot.sendMessage(
                    ChatId.fromId(context.userId),
                    "Invalid input. Please type 'yes' or 'no' to confirm or deny registration."
                )
            }
        }
    }
}
