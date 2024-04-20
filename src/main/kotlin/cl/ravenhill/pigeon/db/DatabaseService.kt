package cl.ravenhill.pigeon.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Manages the database connection for an application. This class encapsulates the details required to
 * establish a connection to a specified database using JDBC. It allows initializing and accessing
 * the database connection throughout the application.
 *
 * ## Usage:
 * Instantiate this class with specific JDBC URL and driver name parameters. Then, initialize the database connection
 * at the start of your application to ensure that the database operations can be performed using the `database` property.
 *
 * ### Example:
 * ```kotlin
 * fun main() {
 *     val databaseService = DatabaseService("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "org.h2.Driver")
 *     databaseService.init()  // Initializes the database connection
 *     // Application code that uses the databaseService.database for operations
 * }
 * ```
 *
 * @property jdbcUrl The JDBC URL used for connecting to the database. This should include the database type,
 *                   host, port, and database name, structured according to the JDBC URL format.
 * @property driverName The fully qualified name of the JDBC driver used to establish the connection.
 *                      This must be available in the classpath of the application.
 */
class DatabaseService(private val jdbcUrl: String, private val driverName: String) {
    lateinit var database: Database
        private set  // Ensures that the database property can only be set within this class.

    /**
     * Initializes the database connection based on the provided JDBC URL and driver name. This method
     * sets up the `database` property that can then be used for executing database operations.
     *
     * @throws IllegalStateException if the database connection fails or the parameters are incorrect.
     */
    fun init(): DatabaseService {
        database = Database.connect(jdbcUrl, driverName)
        transaction(database) {
            SchemaUtils.create(Meta, Users, Admins)
        }
        return this
    }
}
