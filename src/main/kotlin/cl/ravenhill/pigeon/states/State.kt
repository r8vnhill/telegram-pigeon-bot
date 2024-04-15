package cl.ravenhill.pigeon.states

import cl.ravenhill.pigeon.chat.PigeonUser
import org.slf4j.LoggerFactory

sealed class State(protected val context: PigeonUser) {
    private val logger = LoggerFactory.getLogger(javaClass)

    open fun onStart(): TransitionResult {
        logger.warn("User ${context.username.ifBlank { context.id }} tried to start from state ${javaClass.simpleName}")
        return TransitionFailure
    }

    open fun process() {
        logger.debug("Processing state ${javaClass.simpleName}")
    }
}
