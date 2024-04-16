package cl.ravenhill.pigeon.states

import cl.ravenhill.pigeon.arbs.arbUser
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class StartStateTest : FreeSpec({

    "When constructing with a PigeonUser, it should set the context property" {
        checkAll(arbUser()) { user ->
            val state = StartState(user)
            state.context shouldBe user
            state.context.state shouldBe state
        }
    }

    "On start, it should return a transition error" {
    }
})
