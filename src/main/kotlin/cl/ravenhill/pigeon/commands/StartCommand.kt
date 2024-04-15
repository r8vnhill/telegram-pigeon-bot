package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.chat.ChatId
import cl.ravenhill.pigeon.chat.PigeonUser
import cl.ravenhill.pigeon.db.Users
import cl.ravenhill.pigeon.states.TransitionFailure
import cl.ravenhill.pigeon.states.TransitionSuccess
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ParseMode
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.io.File

private val logger = LoggerFactory.getLogger("StartCommand")

data class StartCommand(
    override val user: PigeonUser,
    val bot: Bot
) : Command {
    override val name: String = "start"
    override val parameters: List<String> = listOf()

    override fun execute(): CommandResult {
        val result = transaction {
            when (Users.selectAll().where { Users.id eq user.id }.count()) {
                0L -> {
                    logger.info("User ${user.username} started Pigeon")
                    val welcomeFile = File("messages/welcome_message.md")
                    if (welcomeFile.exists()) {
                        bot.sendMessage(
                            ChatId.fromId(user.id),
                            welcomeFile.readText(),
                            parseMode = ParseMode.MARKDOWN
                        )
                        CommandSuccess(user, "Pigeon started")
                    } else {
                        CommandFailure(user, "Welcome message not found")
                    }
                }

                else -> {
                    logger.info("User ${user.username} already started Pigeon")
                    bot.sendMessage(ChatId.fromId(user.id), "Welcome back to Pigeon!")
                    CommandSuccess(user, "Pigeon already started")
                }
            }
        }
        return result
    }

    companion object {
        context(Dispatcher)
        fun registerCommand() {
            command("start") {
                StartCommand(user = PigeonUser.from(message.from!!), bot = bot).execute()
            }
        }
    }
}
