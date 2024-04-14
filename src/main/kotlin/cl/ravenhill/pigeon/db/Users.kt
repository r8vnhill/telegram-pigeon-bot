package cl.ravenhill.pigeon.db

import org.jetbrains.exposed.dao.id.IntIdTable


/**
 * Represents the `Users` table in the database with the Exposed framework. This object extends
 * `IntIdTable`, indicating that this table uses an integer as the ID column, which Exposed
 * automatically manages. The table includes columns for storing usernames and chat IDs associated
 * with users of the Telegram pigeon bot.
 *
 * ## Usage:
 * This table object is used directly in database operations to reference table columns in SQL
 * transactions, facilitating the interaction with the user data stored in the database.
 *
 * ### Example 1: Inserting a new user
 * ```kotlin
 * transaction {
 *     Users.insert {
 *         it[username] = "new_user"
 *         it[chatId] = 123456789L
 *     }
 * }
 * ```
 * ### Example 2: Querying users by username
 * ```kotlin
 * val query = transaction {
 *     Users.select { Users.username eq "existing_user" }
 *     .map { it[Users.username] }
 * }
 * println(query)
 * ```
 *
 * This object is typically used in DAO (Data Access Object) implementations or directly in service
 * classes that manage business logic interacting with user data.
 *
 * @property username
 *  Represents the `username` column in the `Users` table, defined as a VARCHAR of length 50.
 * @property chatId
 *  Represents the `chat_id` column in the `Users` table, defined as a LONG type column.
 */
object Users : IntIdTable() {
    val username = varchar("username", 50)
    val chatId = long("chat_id")
}
