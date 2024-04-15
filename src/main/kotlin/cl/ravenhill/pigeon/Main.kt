package cl.ravenhill.pigeon

import cl.ravenhill.jakt.Jakt.constraints
import cl.ravenhill.jakt.constraints.longs.BeEqualTo
import cl.ravenhill.jakt.exceptions.CompositeException
import cl.ravenhill.pigeon.chat.ChatId
import cl.ravenhill.pigeon.chat.PigeonUser
import cl.ravenhill.pigeon.commands.Failure
import cl.ravenhill.pigeon.commands.StartCommand
import cl.ravenhill.pigeon.commands.Success
import cl.ravenhill.pigeon.db.Admins
import cl.ravenhill.pigeon.db.DatabaseService
import cl.ravenhill.pigeon.db.Meta
import cl.ravenhill.pigeon.db.Users
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

private val logger = LoggerFactory.getLogger("Main")

@OptIn(ExperimentalTime::class)
fun main() {
    initDatabase()
    logger.info("Setting up bot")
    val (bot, botStartupTime) = measureTimedValue {
        bot {
            token = queryApiKey()
            registerCommands()
        }
    }
    logger.info("Bot setup in $botStartupTime")
    logger.info("Start polling")
    bot.startPolling()
}

/**
 * Initializes the database and creates the necessary tables. It logs the start and completion of the database
 * initialization process, as well as the time taken to complete the initialization.
 *
 * The function is annotated with `@OptIn(ExperimentalTime::class)` to use the experimental `measureTime` function
 * from Kotlin's time measurement API, which provides a concise and readable way to measure and log the time taken
 * to execute database initialization tasks.
 *
 * ## Usage:
 * This function should be called at the start of the application lifecycle to set up the database before any operations
 * that require database access are performed.
 *
 * @receiver None Explicit receiver is not defined, assuming function is enclosed within a class with access to a logger.
 */
@OptIn(ExperimentalTime::class)
private fun initDatabase() {
    logger.info("Initializing database")
    measureTime {
        DatabaseService.init() // Initializes connection or a set of tables
        transaction { SchemaUtils.create(Meta, Users, Admins) } // Creates tables in the database
    }.also { timeTaken ->
        logger.info("Database initialized in $timeTaken")
    }
}

/**
 * Retrieves the API key from the `Meta` table of the database specifically where the key column equals "API_KEY".
 * This function ensures that exactly one entry for "API_KEY" is present in the database and returns its associated
 * value.
 * If the constraint is not met (i.e., if "API_KEY" is not present exactly once), an exception will be thrown.
 *
 * @return Returns the string value of the API key if found and valid.
 * @throws CompositeException If the constraint for the presence of "API_KEY" is not met.
 */
private fun queryApiKey(): String = transaction {
    val result = Meta.selectAll()
        .where { Meta.key eq "API_KEY" }
    constraints {
        "API_KEY must be present in meta table" { result.count() must BeEqualTo(1L) }
    }
    result.single()[Meta.value]
}

context(Bot.Builder)
fun registerCommands() {
    dispatch {
        command("start") {
            val user = PigeonUser(message.from!!.username!!, message.chat.id)
            when (val result = StartCommand(user).execute()) {
                is Success -> bot.sendMessage(ChatId.fromId(message.chat.id), result.message)
                is Failure -> bot.sendMessage(
                    ChatId.fromId(message.chat.id),
                    result.message
                )
            }
        }
    }
}
