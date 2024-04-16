package cl.ravenhill.pigeon.states

import cl.ravenhill.pigeon.chat.PigeonUser
import com.github.kotlintelegrambot.Bot
import org.slf4j.LoggerFactory

sealed class State(protected val context: PigeonUser) {
    private val logger = LoggerFactory.getLogger(javaClass)

    open fun onStart(bot: Bot): TransitionResult {
        logger.warn("User ${context.username.ifBlank { context.userId }} tried to start from state ${javaClass.simpleName}")
        return TransitionFailure
    }

    open fun process(text: String?, bot: Bot) {
        logger.debug("Processing state ${javaClass.simpleName}")
    }
}
