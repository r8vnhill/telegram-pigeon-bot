package cl.ravenhill.pigeon.commands

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import cl.ravenhill.pigeon.arbBot
import cl.ravenhill.pigeon.arbUser
import cl.ravenhill.pigeon.chat.ReadUser
import cl.ravenhill.pigeon.db.DatabaseService
import cl.ravenhill.pigeon.db.addUser
import cl.ravenhill.pigeon.db.deleteUser
import cl.ravenhill.pigeon.states.StartState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.checkAll
import org.slf4j.LoggerFactory

class StartCommandTest : FreeSpec({

    lateinit var service: DatabaseService
    val logger = LoggerFactory.getLogger("Exposed") as Logger
    val originalLevel = logger.level

    beforeTest {
        service = DatabaseService("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "org.h2.Driver")
        logger.level = Level.OFF
    }

    afterTest {
        logger.level = originalLevel
    }

    "A StartCommand" - {
        "should have a user" {
            checkAll(arbUser(), arbBot()) { user, bot ->
                val command = StartCommand(user, bot, service)
                command.user shouldBe user
            }
        }

        "when the user is already registered" - {
            "should return a success if the welcome message is sent" {
                checkAll(arbUser(), arbBot(Arb.constant(true))) { user, bot ->
                    withUsers(service, listOf(user)) {
                        val command = StartCommand(user, bot, service)
                        val result = command.execute()
                        result shouldBe CommandSuccess(user, "User already exists in the database")
                    }
                }
            }

            "should return a failure message if the welcome message fails to send" {
                checkAll(arbUser(), arbBot(Arb.constant(false))) { user, bot ->
                    withUsers(service, listOf(user)) {
                        val command = StartCommand(user, bot, service)
                        val result = command.execute()
                        result shouldBe CommandFailure(user, "Failed to send welcome back message")
                    }
                }
            }
        }

        "when the user is not registered" - {
            "should return a success and transition to the start state" {
                checkAll(arbUser(), arbBot(Arb.constant(true))) { user, bot ->
                    withUsers(service, emptyList()) {
                        val command = StartCommand(user, bot, service)
                        val result = command.execute()
                        result shouldBe CommandSuccess(
                            user,
                            "User does not exist in the database, welcome message sent"
                        )
                        user.state::class shouldBe StartState::class
                    }
                }
            }
        }
    }
})

private fun withUsers(service: DatabaseService, users: List<ReadUser>, block: () -> Unit) {
    service.init()
    users.forEach { service.addUser(it) }
    block()
    users.forEach { service.deleteUser(it) }
}
