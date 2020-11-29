package nl.juraji.biliomi.utils

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

internal class KMySQLContainer(image: String) : MySQLContainer<KMySQLContainer>(image)

@Testcontainers
@SpringBootTest
abstract class DatabaseTest {

    companion object {

        @Container
        private val axonContainer = KMySQLContainer("mysql").apply {
            withDatabaseName("axon")
            withUsername("sa")
            withPassword("sa")
            start()
        }

        @Container
        private val projectionsContainer = KMySQLContainer("mysql").apply {
            withDatabaseName("projections")
            withUsername("sa")
            withPassword("sa")
            start()
        }

        @Container
        private val securityContainer = KMySQLContainer("mysql").apply {
            withDatabaseName("security")
            withUsername("sa")
            withPassword("sa")
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { axonContainer.jdbcUrl }
            registry.add("spring.datasource.password", axonContainer::getPassword)
            registry.add("spring.datasource.username", axonContainer::getUsername)

            registry.add("projections.datasource.url") { projectionsContainer.jdbcUrl }
            registry.add("projections.datasource.username", projectionsContainer::getUsername)
            registry.add("projections.datasource.password", projectionsContainer::getPassword)

            registry.add("security.datasource.url") { securityContainer.jdbcUrl }
            registry.add("security.datasource.username", securityContainer::getUsername)
            registry.add("security.datasource.password", securityContainer::getPassword)
        }
    }
}
