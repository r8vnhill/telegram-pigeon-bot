package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.arbBot
import cl.ravenhill.pigeon.arbUser
import cl.ravenhill.pigeon.db.DatabaseService
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class StartCommandTest : FreeSpec({

    lateinit var service: DatabaseService

    beforeTest {
        service = DatabaseService("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "org.h2.Driver")
    }

    "A StartCommand" - {
        "should have a user" {
            checkAll(arbUser(), arbBot()) { user, bot ->
                val command = StartCommand(user, bot, service)
                command.user shouldBe user
            }
        }
    }
})
