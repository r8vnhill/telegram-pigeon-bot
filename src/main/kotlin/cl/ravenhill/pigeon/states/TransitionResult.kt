package cl.ravenhill.pigeon.states

sealed interface TransitionResult

data object TransitionSuccess : TransitionResult

data object TransitionFailure : TransitionResult
