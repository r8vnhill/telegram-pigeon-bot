package cl.ravenhill.pigeon.commands

import cl.ravenhill.pigeon.chat.PigeonUser
import cl.ravenhill.pigeon.db.Users
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

data class StartCommand(override val user: PigeonUser) : Command {
    override val name: String = "start"
    override val parameters: List<String> = listOf()

    override fun execute(): CommandResult = transaction {
        Users.selectAll().where { Users.id eq user.id }.count().let {
            if (it == 0L) {
                Failure(user, "Welcome! Please register using the /register command.")
            } else {
                Success(user, "Welcome back!")
            }
        }
    }
}
