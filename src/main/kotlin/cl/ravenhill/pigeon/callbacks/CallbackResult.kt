package cl.ravenhill.pigeon.callbacks

/**
 * Represents the result of a callback operation within the pigeon system. This sealed interface ensures that all
 * callback results are handled in a uniform manner while allowing distinct implementations that can specify more
 * detailed behavior or properties associated with the result of a callback operation.
 *
 * @property message
 *  A descriptive message associated with the callback result. This message typically provides feedback or details about
 *  the outcome of the callback.
 */
sealed interface CallbackResult {
    val message: String
}

/**
 * Represents a successful outcome of a callback operation. This class provides a more specific
 * context for success by carrying a message detailing the successful outcome.
 *
 * @param message A message that describes the success of the callback operation.
 */
data class CallbackSuccess(override val message: String) : CallbackResult

/**
 * Represents a failure in a callback operation. This class encapsulates the error state by
 * carrying a message that details the reason or nature of the failure.
 *
 * @param message A message that describes the failure of the callback operation.
 */
data class CallbackFailure(override val message: String) : CallbackResult
