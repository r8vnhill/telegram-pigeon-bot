package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.User
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.arbs.firstName
import io.kotest.property.arbs.lastName
import io.kotest.property.arbs.usernames
import io.kotest.property.checkAll

class CommandResultTest : FreeSpec({
    "A Success command result can be created" {
        checkAll(user(), Arb.string()) { user, message ->
            with(Success(user, message)) {
                this.user shouldBe user
                this.message shouldBe message
            }
        }
    }

    "A Failure command result can be created" {
        checkAll(user(), Arb.string()) { user, message ->
            with(Failure(user, message)) {
                this.user shouldBe user
                this.message shouldBe message
            }
        }
    }
})

private fun user() = arbitrary {
    User(
        Arb.long().bind(),
        Arb.boolean().bind(),
        Arb.firstName().bind().name,
        Arb.lastName().bind().name,
        Arb.usernames().bind().value
    )
}