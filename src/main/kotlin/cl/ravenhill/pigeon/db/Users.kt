package cl.ravenhill.pigeon.db

import cl.ravenhill.pigeon.db.Users.chatId
import cl.ravenhill.pigeon.db.Users.username
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

/**
 * Represents the `Users` table in the database, using the Exposed SQL framework. This object extends
 * `IdTable<Long>`, using a long integer as the primary key column, specifically the chat ID in this context,
 * which is unique for each user. It includes columns for storing usernames and the corresponding chat IDs.
 *
 * ## Usage:
 * This object facilitates database interactions by providing a structured way to reference and manipulate user data.
 * It is essential in data access layers or service classes where user information is frequently queried or updated.
 *
 * ### Example 1: Inserting a New User
 * Demonstrates how to insert a new user into the `Users` table:
 * ```kotlin
 * transaction {
 *     Users.insert {
 *         it[username] = "new_user"
 *         it[chatId] = 123456789L
 *     }
 * }
 * ```
 * ### Example 2: Querying Users by Username
 * Illustrates how to retrieve users from the `Users` table by their username:
 * ```kotlin
 * val userNames = transaction {
 *     Users.select { Users.username eq "existing_user" }
 *         .map { it[Users.username] }
 * }
 * userNames.forEach { println(it) }
 * ```
 *
 * The `Users` object is commonly utilized within DAO (Data Access Object) patterns or in service layers
 * to manage and query user data effectively.
 *
 * @property username The column representing the user's username, defined as a VARCHAR of length 50. This is used
 *                    to store the user's chosen username.
 * @property chatId The primary key of the table, representing the user's Telegram chat ID as a unique identifier,
 *                  defined as a LONG type. This field is used both as a unique identifier and the primary key of the table.
 */
object Users : IdTable<Long>() {
    // Column definitions
    val chatId = long("chat_id")
    val username = varchar("username", 50)
    val state = varchar("state", 50)

    // Custom primary key definition
    override val id: Column<EntityID<Long>> = chatId.entityId()
    override val primaryKey = PrimaryKey(id, name = "PK_ChatId") // Explicitly naming the primary key for clarity
}