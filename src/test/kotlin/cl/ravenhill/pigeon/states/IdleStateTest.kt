package cl.ravenhill.pigeon.states

import cl.ravenhill.pigeon.arbBot
import cl.ravenhill.pigeon.arbs.arbUser
import com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class IdleStateTest : FreeSpec({

    "Can be constructed with an user" {
        checkAll(arbUser()) { user ->
            val state = IdleState(user)
            state.context shouldBe user
        }
    }

    "On start, it transitions to StartState" {
        checkAll(arbIdleState(), arbBot()) { state, bot ->
            state.onStart(bot) shouldBe TransitionSuccess
            state.context.state shouldBe StartState(state.context)
        }
    }

    "On idle, it transitions to IdleState" {
        checkAll(arbIdleState(), arbBot()) { state, bot ->
            state.onIdle(bot) shouldBe TransitionSuccess
            state.context.state shouldBe IdleState(state.context)
        }
    }

    "On process, it logs the state" {
        checkAll(arbIdleState(), arbBot(), Arb.string().orNull()) { state, bot, text ->
            tapSystemOut {
                state.process(text, bot)
            } shouldContain "Processing state IdleState"
        }
    }
})

private fun arbIdleState() = arbitrary {
    IdleState(arbUser().bind())
}
