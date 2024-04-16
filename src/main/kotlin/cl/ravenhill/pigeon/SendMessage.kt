package cl.ravenhill.pigeon

import cl.ravenhill.pigeon.chat.ChatId
import cl.ravenhill.pigeon.chat.ReadUser
import com.github.kotlintelegrambot.Bot

fun sendMessage(bot: Bot, message: String, user: ReadUser): BotResult =
    bot.sendMessage(ChatId.fromId(user.userId), message).fold(
        ifSuccess = {
            BotSuccess("Message sent to user ${user.username.ifBlank { user.userId.toString() }}: $message")
        },
        ifError = {
            BotFailure("Failed to send message to user ${user.username.ifBlank { user.userId.toString() }}")
        }
    )