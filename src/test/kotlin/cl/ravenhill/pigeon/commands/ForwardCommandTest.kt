package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.arbs.arbUser
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbs.games.cluedoAccusations
import io.kotest.property.checkAll

class ForwardCommandTest : FreeSpec({
    "Transforming" - {
        "to String" {
            checkAll(arbForwardCommand()) { command ->
                with(command) {
                    val expected = "ForwardCommand(message=$message, user=${user.username}, parameters=$parameters)"
                    toString() shouldBe expected
                }
            }
        }
    }

    "Executing" - {
        "should return a success result" {
            checkAll(arbForwardCommand()) { command ->
                with(command) {
                    val result = execute()
                    result.user shouldBe user
                    result.message shouldBe "Message forwarded successfully"
                }
            }
        }
    }
})

private fun arbForwardCommand() = arbitrary {
    ForwardCommand(
        message = Arb.cluedoAccusations().bind().weapon.name,
        arbUser().bind(),
        Arb.list(Arb.cluedoAccusations().map {
            it.weapon.name
        }).bind()
    )
}