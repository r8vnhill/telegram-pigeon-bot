package cl.ravenhill.pigeon.db

import org.jetbrains.exposed.sql.Database

object DatabaseService {
    private const val JDBC_URL = "jdbc:h2:file:./build/pigeon"
    private const val DRIVER_NAME = "org.h2.Driver"
    val database = Database.connect(JDBC_URL, driver = DRIVER_NAME)
}
