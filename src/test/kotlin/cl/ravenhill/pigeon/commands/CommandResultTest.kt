package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.arbs.arbUser
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class CommandResultTest : FreeSpec({
    "A Success command result can be created" {
        checkAll(arbUser(), Arb.string()) { user, message ->
            with(Success(user, message)) {
                this.user shouldBe user
                this.message shouldBe message
            }
        }
    }

    "A Failure command result can be created" {
        checkAll(arbUser(), Arb.string()) { user, message ->
            with(Failure(user, message)) {
                this.user shouldBe user
                this.message shouldBe message
            }
        }
    }
})

