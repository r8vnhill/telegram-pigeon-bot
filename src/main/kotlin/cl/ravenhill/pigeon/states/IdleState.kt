package cl.ravenhill.pigeon.states

import cl.ravenhill.pigeon.chat.PigeonUser

class IdleState(context: PigeonUser) : State(context) {
    override fun onStart(): TransitionResult {
        context.state = StartState(context)
        return TransitionSuccess
    }
}
