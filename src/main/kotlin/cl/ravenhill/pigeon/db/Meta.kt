package cl.ravenhill.pigeon.db

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Meta : IdTable<String>() {
    val key = varchar("key", 50)  // Column for the metadata key
    val value = varchar("value", 50)  // Column for the metadata value
    override val id: Column<EntityID<String>> = key.entityId()
    override val primaryKey = PrimaryKey(key, name = "PK_MetaKey")  // Primary key definition
}
