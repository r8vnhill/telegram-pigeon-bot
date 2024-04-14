package cl.ravenhill.pigeon

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId

fun main() {
    val bot = bot {
        token = java.io.File(".secret").readText()
        dispatch {
            command("forward") {
                val result = bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Forwarding message...")
                result.fold({
                    println("[${it.chat.id}]: ${it.text}")
                }, {
                    println("Error")
                })
            }
        }
    }.startPolling()
}