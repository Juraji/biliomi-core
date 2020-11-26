package nl.juraji.biliomi.configuration.db.projections

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.dialect.MySqlDialect
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.core.DatabaseClient
import javax.annotation.PostConstruct


@Configuration
@EnableR2dbcRepositories(
        entityOperationsRef = "ordersEntityTemplate"
)
class ProjectionsDataSourceConfiguration(
        private val dataSourceProperties: ProjectionsDataSourceProperties,
        private val flywayProperties: ProjectionsFlywayProperties,
) {

    @PostConstruct
    fun initialize() {
        val flyway = Flyway.configure()
                .baselineOnMigrate(flywayProperties.isBaselineOnMigrate)
                .dataSource(flywayProperties.url, flywayProperties.user, flywayProperties.password)
                .locations(*flywayProperties.locations.toTypedArray())
                .load()

        flyway.migrate()
    }

    @Bean
    @Qualifier("projectionsConnectionFactory")
    fun projectionsConnectionFactory(): ConnectionFactory {
        val options = ConnectionFactoryOptions.parse(dataSourceProperties.url).mutate()
                .option(ConnectionFactoryOptions.USER, dataSourceProperties.username)
                .option(ConnectionFactoryOptions.PASSWORD, dataSourceProperties.password)
                .build()

        return ConnectionFactories.get(options)
    }

    @Bean
    @Qualifier("projectionsEntityTemplate")
    fun projectionsEntityTemplate(
            @Qualifier("projectionsConnectionFactory") connectionFactory: ConnectionFactory,
    ): R2dbcEntityOperations {
        val strategy = DefaultReactiveDataAccessStrategy(MySqlDialect.INSTANCE)
        val databaseClient: DatabaseClient = DatabaseClient.builder()
                .connectionFactory(connectionFactory)
                .bindMarkers(MySqlDialect.INSTANCE.bindMarkersFactory)
                .build()

        return R2dbcEntityTemplate(databaseClient, strategy)
    }
}

@ConstructorBinding
@ConfigurationProperties(prefix = "projections.datasource")
data class ProjectionsDataSourceProperties(
        val url: String,
        val username: String,
        val password: String,
)

@ConfigurationProperties(prefix = "projections.flyway")
class ProjectionsFlywayProperties : FlywayProperties()
