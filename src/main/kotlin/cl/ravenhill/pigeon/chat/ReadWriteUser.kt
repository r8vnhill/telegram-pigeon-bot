package cl.ravenhill.pigeon.chat

import cl.ravenhill.pigeon.states.State

/**
 * An extension of the `ReadUser` interface that incorporates write capabilities, specifically
 * allowing the dynamic modification of the user's state. This interface caters to user objects
 * that need the ability to read from and write to their state properties, facilitating dynamic
 * interactions within the system. It is ideally suited for scenarios where user states are expected
 * to change in response to interactions or other system events during a session.
 *
 * ## Usage:
 * Implement this interface in user classes that require the ability to undergo state transitions
 * dynamically. This is particularly useful in complex systems where user behavior directly influences
 * their state, requiring the system to adapt and respond to these changes in real-time.
 *
 * ### Example: Implementing the ReadWriteUser for a session-based user
 * ```kotlin
 * class SessionUser(override val username: String, override val userId: Long) : ReadWriteUser {
 *     override var state: State = IdleState(this)
 *
 *     override fun toTelegramUser(): TelegramUser {
 *         return TelegramUser(id = userId, isBot = false, firstName = "", username = username)
 *     }
 *
 *     override fun onStart(bot: Bot): TransitionResult {
 *         state = ActiveState(this)
 *         return super.onStart(bot) // Calls the ReadUser implementation which handles database logging
 *     }
 *
 *     override fun onIdle(bot: Bot): TransitionResult {
 *         state = IdleState(this)
 *         return super.onIdle(bot) // Calls the ReadUser implementation which updates the state in the database
 *     }
 * }
 * ```
 *
 * @property state
 *  A mutable property representing the current state of the user. This property can be updated in response to user
 *  interactions or other system events, reflecting the dynamic nature of the user's session within the system.
 */
interface ReadWriteUser : ReadUser {
    override var state: State // Overrides the `state` property from `ReadUser` to allow modifications.
}
