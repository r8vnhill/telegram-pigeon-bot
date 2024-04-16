package cl.ravenhill.pigeon.states

import cl.ravenhill.pigeon.chat.PigeonUser
import com.github.kotlintelegrambot.Bot

class IdleState(context: PigeonUser) : State(context) {
    override fun onStart(bot: Bot): TransitionResult {
        context.state = StartState(context)
        return TransitionSuccess
    }
}
