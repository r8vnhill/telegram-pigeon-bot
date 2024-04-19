package cl.ravenhill.pigeon.db

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable

object Admins : IdTable<Long>() {
    val chatId = long("chat_id")
    val username = varchar("username", 50)

    override val id = chatId.entityId()
    override val primaryKey = PrimaryKey(id, name = "PK_ChatId")
}
