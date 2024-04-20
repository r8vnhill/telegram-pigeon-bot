package cl.ravenhill.pigeon

import cl.ravenhill.pigeon.bot.Bot
import cl.ravenhill.pigeon.chat.PigeonUser
import cl.ravenhill.pigeon.chat.ReadUser
import com.github.kotlintelegrambot.entities.ReplyMarkup
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.long
import io.kotest.property.arbs.usernames

const val TEST_DB_FILE = "./build/test"
const val TEST_DB_URL = "jdbc:h2:file:$TEST_DB_FILE"

/**
 * Generates an arbitrary `Bot` object for property-based testing. This function leverages the Kotest property-based
 * testing framework to create bots with a randomized token. The token is generated to match a specific pattern suitable
 * for typical bot tokens, ensuring the bot objects are realistic for testing scenarios.
 */
fun arbBot(): Arb<Bot> = Arb.constant(
    object : Bot {
        override fun sendMessage(user: ReadUser, message: String, replyMarkup: ReplyMarkup?) =
            BotSuccess("Message sent to ${user.username}: $message")
    }
)

/**
 * Generates an arbitrary `PigeonUser` object using property-based testing libraries. This function is typically
 * used in tests where user entities with randomized attributes are required to ensure the robustness
 * and reliability of the system under test.
 */
fun arbUser() = arbitrary {
    PigeonUser(username = Arb.usernames().bind().value, userId = Arb.long().bind())
}