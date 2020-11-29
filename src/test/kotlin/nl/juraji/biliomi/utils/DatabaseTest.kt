package nl.juraji.biliomi.utils

import nl.juraji.biliomi.utils.extensions.uuid
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

internal class KMySQLContainer : MySQLContainer<KMySQLContainer>("mysql")

internal fun container(block: KMySQLContainer.() -> Unit): KMySQLContainer = KMySQLContainer().apply(block)

@Testcontainers
@SpringBootTest
abstract class DatabaseTest {

    companion object {
        private val dbUsername: String = uuid()
        private val dbPassword: String = uuid()

        @Container
        private val axonContainer = container {
            withDatabaseName("axon")
            withUsername(dbUsername)
            withPassword(dbPassword)
            start()
        }

        @Container
        private val projectionsContainer = container {
            withDatabaseName("projections")
            withUsername(dbUsername)
            withPassword(dbPassword)
            start()
        }

        @Container
        private val securityContainer = container {
            withDatabaseName("security")
            withUsername(dbUsername)
            withPassword(dbPassword)
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {

            registry.add("database-test.username") { dbUsername }
            registry.add("database-test.password") { dbPassword }

            registry.add("database-test.axon-jdbc-url") { axonContainer.jdbcUrl }
            registry.add("database-test.projections-jdbc-url") { projectionsContainer.jdbcUrl }
            registry.add("database-test.security-jdbc-url") { securityContainer.jdbcUrl }
        }
    }
}
