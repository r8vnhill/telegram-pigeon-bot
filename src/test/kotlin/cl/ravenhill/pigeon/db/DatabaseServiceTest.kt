package cl.ravenhill.pigeon.db

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import cl.ravenhill.pigeon.chat.PigeonUser
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.sql.exposedLogger
import org.slf4j.LoggerFactory

class DatabaseServiceTest : FreeSpec({

    val logger = LoggerFactory.getLogger(exposedLogger.name) as Logger
    logger.level = Level.OFF

    "The database service" - {
        "should be able to add, get, and delete users" - {
            withData(
                listOf(
                    listOf(PigeonUser("test 1", 1)),
                    listOf(PigeonUser("test 1", 1), PigeonUser("test 2", 2)),
                    listOf(PigeonUser("test 1", 1), PigeonUser("test 2", 2), PigeonUser("test 3", 3))
                )
            ) { users ->
                val service = DatabaseService("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "org.h2.Driver")
                service.init()
                users.forEach { user ->
                    service.addUser(user)
                }
                users.forEach {
                    service.getUser(it) shouldBe it
                }
                users.forEach { user ->
                    service.deleteUser(user)
                }
                users.forEach { user ->
                    service.getUser(user) shouldBe null
                }
            }
        }

        "should be able to update users" - {
            withData(
                listOf(PigeonUser("test 1", 1)),
                listOf(PigeonUser("test 1", 1), PigeonUser("test 2", 2)),
                listOf(PigeonUser("test 1", 1), PigeonUser("test 2", 2), PigeonUser("test 3", 3))
            ) { users ->
                val service = DatabaseService("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "org.h2.Driver")
                service.init()
                users.forEach { user ->
                    service.addUser(user)
                }
                users.forEach { user ->
                    service.updateUser(user.copy(username = "updated ${user.username}"))
                }
                users.forEach { user ->
                    service.getUser(user) shouldBe user.copy(username = "updated ${user.username}")
                }
                users.forEach { user ->
                    service.deleteUser(user)
                }
            }
        }
    }
})
