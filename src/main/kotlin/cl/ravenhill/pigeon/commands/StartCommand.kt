package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.chat.PigeonUser
import cl.ravenhill.pigeon.db.Users
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.io.File


data class StartCommand(
    override val user: PigeonUser,
    val bot: Bot
) : Command {
    private val logger = LoggerFactory.getLogger(javaClass)
    override val name: String = "start"
    override val parameters: List<String> = listOf()

    override fun execute(): CommandResult {
        logger.info("User ${user.username.ifBlank { user.userId }} started the bot")
        val result = transaction {
            if (Users.selectAll().where { Users.id eq user.userId }.count() == 0L) {
                val welcomeFile = File("messages/welcome_message.md")
                if (welcomeFile.exists()) {
                    bot.sendMessage(ChatId.fromId(user.userId), welcomeFile.readText(), parseMode = ParseMode.MARKDOWN)
                    user.onStart(bot)
                    CommandSuccess(user, "User does not exist in the database, welcome message sent")
                } else {
                    CommandFailure(user, "Welcome message not found")
                }
            } else {
                bot.sendMessage(ChatId.fromId(user.userId), "Welcome back!", parseMode = ParseMode.MARKDOWN)
                CommandSuccess(user, "User already exists in the database")
            }
        }
        logger.info("Start command result: $result")
        return result
    }
}
