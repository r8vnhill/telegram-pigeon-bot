package cl.ravenhill.pigeon

import cl.ravenhill.pigeon.commands.ForwardCommand
import cl.ravenhill.pigeon.commands.ForwardCommand.Companion.register
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId

fun main() {
    val bot = bot {
        token = java.io.File(".secret").readText()
        dispatch {
            ForwardCommand.register()   // TODO
        }
    }.startPolling()
}