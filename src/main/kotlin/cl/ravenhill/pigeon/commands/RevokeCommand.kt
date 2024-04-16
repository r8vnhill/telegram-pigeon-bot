package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.callbacks.RevokeConfirmationNo
import cl.ravenhill.pigeon.callbacks.RevokeConfirmationYes
import cl.ravenhill.pigeon.callbacks.StartConfirmationNo
import cl.ravenhill.pigeon.chat.ChatId
import cl.ravenhill.pigeon.chat.ReadUser
import cl.ravenhill.pigeon.db.Users
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

data class RevokeCommand(
    override val user: ReadUser,
    override val bot: Bot
) : Command {
    private val logger = LoggerFactory.getLogger(javaClass)
    override val name: String = "revoke"
    override val parameters: List<String> = listOf()

    override fun execute(): CommandResult {
        logger.info("User ${user.username.ifBlank { user.userId.toString() }} revoked the bot")
        val result = transaction {
            if (Users.selectAll().where { Users.id eq user.userId }.count() == 0L) {
                bot.sendMessage(ChatId.fromId(user.userId), "User does not exist in the database, cannot revoke")
                CommandFailure(user, "User does not exist in the database, cannot revoke")
            } else {
                val message = "Are you sure you want to revoke your registration?"
                bot.sendMessage(ChatId.fromId(user.userId), message, replyMarkup = inlineKeyboardMarkup())
                user.onRevoke(bot)
                CommandSuccess(user, "Revoke command sent successfully")
            }
        }
        logger.info("Revoke command result: $result")
        return result
    }

    private fun inlineKeyboardMarkup() = InlineKeyboardMarkup.create(
        listOf(
            listOf(
                InlineKeyboardButton.CallbackData("Yes", RevokeConfirmationYes.name),
                InlineKeyboardButton.CallbackData("No", RevokeConfirmationNo.name)
            )
        )
    )
}