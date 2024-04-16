package cl.ravenhill.pigeon.states

import cl.ravenhill.pigeon.BotResult
import cl.ravenhill.pigeon.BotSuccess
import cl.ravenhill.pigeon.callbacks.CallbackQueryHandler
import cl.ravenhill.pigeon.chat.ReadWriteUser
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.CallbackQuery
import org.slf4j.Logger
import org.slf4j.LoggerFactory

sealed interface State {
    val context: ReadWriteUser
    private val logger: Logger
        get() = LoggerFactory.getLogger(javaClass)

    fun onStart(bot: Bot): TransitionResult {
        logger.warn(
            "User ${context.username.ifBlank { context.userId }} tried to start from state ${javaClass.simpleName}"
        )
        return TransitionFailure
    }

    fun process(text: String?, bot: Bot): BotResult {
        logger.debug("Processing state ${javaClass.simpleName}")
        return BotSuccess("Processing state ${javaClass.simpleName}")
    }

    fun process(callbackQuery: CallbackQueryHandler): BotResult {
        logger.debug("Processing state ${javaClass.simpleName}")
        return BotSuccess("Processing state ${javaClass.simpleName}")
    }

    fun onIdle(bot: Bot): TransitionResult {
        context.state = IdleState(context)
        return TransitionSuccess
    }

    fun onRevoke(bot: Bot): TransitionResult {
        logger.warn(
            "User ${context.username.ifBlank { context.userId }} tried to revoke from state ${javaClass.simpleName}"
        )
        return TransitionFailure
    }
}
