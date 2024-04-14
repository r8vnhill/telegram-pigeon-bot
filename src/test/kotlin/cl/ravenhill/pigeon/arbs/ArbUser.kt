package cl.ravenhill.pigeon.arbs

import cl.ravenhill.pigeon.chat.PigeonUser
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.long
import io.kotest.property.arbs.usernames

/**
 * Generates an arbitrary `PigeonUser` object using property-based testing libraries. This function is typically
 * used in tests where user entities with randomized attributes are required to ensure the robustness
 * and reliability of the system under test.
 */
fun arbUser() = arbitrary {
    PigeonUser(username = Arb.usernames().bind().value, id = Arb.long().bind())
}
