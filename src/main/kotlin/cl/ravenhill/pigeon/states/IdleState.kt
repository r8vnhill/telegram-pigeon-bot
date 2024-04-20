package cl.ravenhill.pigeon.states

import cl.ravenhill.pigeon.bot.Bot
import cl.ravenhill.pigeon.bot.PigeonBot
import cl.ravenhill.pigeon.chat.ReadWriteUser


/**
 * Represents the idle state of a user within the Telegram pigeon bot system. This state is designated for users
 * who are currently inactive or not interacting actively with the bot. This class extends the abstract `State` class,
 * implementing specific behaviors required when a user initiates interaction while in this state, such as transitioning
 * to an active state (`StartState`).
 *
 * ## Usage:
 * `IdleState` is primarily used to manage transitions of user states when they are not actively engaging with the bot.
 * Transitioning them from this state to an active state upon interaction initiation is crucial for dynamic state
 * management,ensuring that user states are updated correctly in response to their interactions.
 *
 * ### Example: Transitioning from IdleState to StartState
 * ```kotlin
 * val user = PigeonUser("exampleUser", 1234567890L)
 * val initialState = IdleState(user)
 * val result = initialState.onStart(bot)  // Initiates transition to StartState
 * println(result)  // Outputs TransitionSuccess, indicating successful state transition
 * ```
 *
 * @property
 *  context The `ReadWriteUser` instance representing the user associated with this state, which provides the necessary
 *  context for state transitions.
 */
data class IdleState(override val context: ReadWriteUser) : State {

    init {
        context.state = this  // Sets the user's state to IdleState upon initialization
    }

    /**
     * Handles the start of interaction with the bot while the user is in an idle state. This method transitions
     * the user to a `StartState`, reflecting the initiation of active interaction with the bot.
     *
     * @param bot The `Bot` instance managing the current user interaction.
     * @return A `TransitionResult` indicating the outcome of the interaction initiation. This method typically returns
     * `TransitionSuccess`, signaling a successful transition to a more active state.
     */
    override fun onStart(bot: Bot): TransitionResult {
        context.state = StartState(context)  // Transition the user to the StartState
        return TransitionSuccess  // Return success upon a successful state transition
    }

    override fun onRevoke(bot: PigeonBot): TransitionResult {
        context.state = RevokeState(context)
        return TransitionSuccess
    }

    override fun toString() = this::class.simpleName ?: "IdleState"
}