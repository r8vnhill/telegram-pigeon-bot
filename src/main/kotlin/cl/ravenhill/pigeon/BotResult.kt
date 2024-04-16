package cl.ravenhill.pigeon

sealed interface BotResult

data class BotSuccess(val message: String) : BotResult

data class BotFailure(val message: String) : BotResult
