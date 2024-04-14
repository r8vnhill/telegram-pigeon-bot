package cl.ravenhill.pigeon.chat

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbs.usernames
import io.kotest.property.checkAll

class PigeonUserTest : FreeSpec({
    "Transforming" - {
        "to a Telegram User" {
            checkAll(Arb.long(), Arb.usernames()) { id, username ->
                val user = PigeonUser(username = username.value, id = id)
                val telegramUser = user.toUser()
                telegramUser.id shouldBe id
                telegramUser.isBot shouldBe false
                telegramUser.username shouldBe username.value
            }
        }
    }
})