package nl.juraji.biliomi.utils

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
abstract class BaseSpringBootTest {

    companion object {

        private const val dbUsername: String = "sa"
        private const val dbPassword: String = "sa"

        @Container
        private val axonContainer = container {
            withDatabaseName("axon")
            withUsername(dbUsername)
            withPassword(dbPassword)
            withReuse(true)
            start()
        }

        @Container
        private val projectionsContainer = container {
            withDatabaseName("projections")
            withUsername(dbUsername)
            withPassword(dbPassword)
            withReuse(true)
            start()
        }

        private fun container(block: KMySQLContainer.() -> Unit): KMySQLContainer = KMySQLContainer().apply(block)

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {

            registry.add("database-test.username") { dbUsername }
            registry.add("database-test.password") { dbPassword }

            registry.add("database-test.axon-jdbc-url") { axonContainer.jdbcUrl }
            registry.add("database-test.projections-jdbc-url") { projectionsContainer.jdbcUrl }
        }
    }

    private class KMySQLContainer : MySQLContainer<KMySQLContainer>("mysql")
}
