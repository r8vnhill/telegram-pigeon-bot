package cl.ravenhill.pigeon

import cl.ravenhill.pigeon.chat.PigeonUser
import cl.ravenhill.pigeon.chat.ReadUser
import cl.ravenhill.pigeon.db.DatabaseService
import cl.ravenhill.pigeon.db.Users
import cl.ravenhill.pigeon.states.IdleState
import com.github.kotlintelegrambot.bot
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.arbs.usernames
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

const val TEST_DB_FILE = "./build/test"
const val TEST_DB_URL = "jdbc:h2:file:$TEST_DB_FILE"

/**
 * Generates an arbitrary `Bot` object for property-based testing. This function leverages the Kotest property-based
 * testing framework to create bots with a randomized token. The token is generated to match a specific pattern suitable
 * for typical bot tokens, ensuring the bot objects are realistic for testing scenarios.
 */
fun arbBot() = arbitrary {
    val token = Arb.stringPattern("[a-zA-Z0-9]{45}").bind()
    bot {
        this.token = token
    }
}

fun arbDatabaseService(users: List<ReadUser>) = arbitrary {
    DatabaseService(TEST_DB_URL, "org.h2.Driver").apply {
        init()
        transaction(database) {
            Users.deleteAll()
            users.forEach { user ->
                Users.insert {
                    it[username] = user.username
                    it[id] = user.userId
                    it[state] = IdleState::class.simpleName!!
                }
            }
        }
    }
}

/**
 * Generates an arbitrary `PigeonUser` object using property-based testing libraries. This function is typically
 * used in tests where user entities with randomized attributes are required to ensure the robustness
 * and reliability of the system under test.
 */
fun arbUser() = arbitrary {
    PigeonUser(username = Arb.usernames().bind().value, userId = Arb.long().bind())
}