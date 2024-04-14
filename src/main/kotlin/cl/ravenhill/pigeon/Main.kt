package cl.ravenhill.pigeon

import cl.ravenhill.pigeon.commands.ForwardCommand
import cl.ravenhill.pigeon.commands.register
import cl.ravenhill.pigeon.db.Admins
import cl.ravenhill.pigeon.db.DatabaseService
import cl.ravenhill.pigeon.db.Users
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction


fun main() {
    bot {
        token = java.io.File(".secret").readText()
        dispatch {
            ForwardCommand.register()
        }
    }.startPolling()
}
