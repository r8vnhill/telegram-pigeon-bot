package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.chat.ChatId
import cl.ravenhill.pigeon.chat.ReadUser
import cl.ravenhill.pigeon.db.Users
import com.github.kotlintelegrambot.Bot
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

//class AddChatCommand(
//    override val user: ReadUser,
//    override val bot: Bot
//) : Command {
//    override val name: String = "addChat"
//    override val parameters: List<String> = emptyList()
//    private val logger = LoggerFactory.getLogger(javaClass)
//
//    override fun execute(): CommandResult {
//        logger.info("User ${user.username.ifBlank { user.userId.toString() }} is adding a chat")
//        val result = transaction {
//            if (Users.selectAll().where { Users.id eq user.userId }.count() == 0L) {
//                bot.sendMessage(ChatId.fromId(user.userId), "User does not exist in the database, cannot add chat")
//            } else {
////                user.onAddChat(bot)
//                CommandSuccess(user, "Add chat command sent successfully")
//            }
//        }
//    }
//}