package cl.ravenhill.pigeon.states

import cl.ravenhill.pigeon.arbBot
import cl.ravenhill.pigeon.arbUser
import com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.arbitrary.arbitrary
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
        checkAll(arbStartState(), arbBot()) { state, bot ->
            tapSystemOut {
                state.onStart(bot) shouldBe TransitionFailure
            }.shouldContain(
                "User ${state.context.username.ifBlank { state.context.userId }} tried to start from state " +
                        "StartState"
            )
        }
    }

    "On idle, it should transition to IdleState" {
        checkAll(arbStartState(), arbBot()) { state, bot ->
            state.onIdle(bot) shouldBe TransitionSuccess
            state.context.state shouldBe IdleState(state.context)
        }
    }
})

private fun arbStartState() = arbitrary {
    StartState(arbUser().bind())
}
