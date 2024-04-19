import cl.ravenhill.pigeon.TEST_DB_FILE
import cl.ravenhill.pigeon.TEST_DB_URL
import cl.ravenhill.pigeon.db.DatabaseService
import cl.ravenhill.pigeon.db.Users
import io.kotest.property.PropTestListener
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

/**
 * A listener object used in property-based testing environments that handles cleanup tasks after each test.
 * Specifically, this listener is responsible for deleting a database file used during tests to ensure that each
 * test runs in a clean state without any leftover data from previous tests.
 */
object CleanupDatabaseListener : PropTestListener {

    /**
     * Performs cleanup after each test by deleting the database file used in tests.
     * This method is called automatically by the testing framework if this listener is registered.
     */
    override suspend fun afterTest() {
        transaction(DatabaseService(TEST_DB_URL, "org.h2.Driver").database) {
            Users.dropStatement()
        }
    }
}