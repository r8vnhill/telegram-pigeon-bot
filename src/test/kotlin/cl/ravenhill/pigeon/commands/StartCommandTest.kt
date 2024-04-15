package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.arbs.arbUser
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class StartCommandTest : FreeSpec({
    "When constructing a Start Command" - {
        "should have the correct name" {
            checkAll(arbUser()) { user ->
                val command = StartCommand(user)
                command.name shouldBe "start"
            }
        }

        "should have no parameters" {
            checkAll(arbUser()) { user ->
                val command = StartCommand(user)
                command.parameters.size shouldBe 0
            }
        }
    }
})
