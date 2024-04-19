import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import io.kotest.property.PropTestListener
import org.slf4j.LoggerFactory

/**
 * A test listener designed to temporarily disable logging for specific components during property-based testing.
 * This listener specifically targets the Exposed SQL library's logger, saving its original logging level before
 * a test and restoring it afterward. This ensures that tests can run without excessive logging noise, which can
 * be particularly useful during automated testing environments or when debugging specific test cases.
 */
object ResetLoggerListener : PropTestListener {
    private const val exposedLoggerName = "Exposed"
    private var originalLoggingLevel: Level

    init {
        val logger = LoggerFactory.getLogger(exposedLoggerName) as Logger
        originalLoggingLevel = logger.level
    }

    /**
     * Temporarily disables logging for the Exposed logger before each test.
     */
    override suspend fun beforeTest() {
        val logger = LoggerFactory.getLogger(exposedLoggerName) as Logger
        logger.level = Level.OFF
    }

    /**
     * Restores the original logging level for the Exposed logger after each test.
     */
    override suspend fun afterTest() {
        val logger = LoggerFactory.getLogger(exposedLoggerName) as Logger
        logger.level = originalLoggingLevel
    }
}