package cl.ravenhill.pigeon.db

import org.jetbrains.exposed.dao.id.IntIdTable

object Admins : IntIdTable() {
    val chatId = long("chat_id")
    val username = varchar("username", 50)
}
