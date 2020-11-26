package nl.juraji.biliomi.configuration.db.projections

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import nl.juraji.biliomi.configuration.db.MultiTenancyConfiguration
import nl.juraji.biliomi.configuration.db.Tenant
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.dialect.MySqlDialect
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.core.DatabaseClient


@Configuration
@EnableR2dbcRepositories(
        entityOperationsRef = "projectionsEntityTemplate",
        basePackages = ["nl.juraji.biliomi.projections.repositories"]
)
class ProjectionsDataSourceConfiguration(
        multiTenancyConfiguration: MultiTenancyConfiguration,
) {
    private val tenant: Tenant = multiTenancyConfiguration.findTenant("projections")

    @Bean
    @Qualifier("projectionsConnectionFactory")
    fun projectionsConnectionFactory(): ConnectionFactory {
        val options = ConnectionFactoryOptions.parse(tenant.datasource.url).mutate()
                .option(ConnectionFactoryOptions.USER, tenant.datasource.username)
                .option(ConnectionFactoryOptions.PASSWORD, tenant.datasource.password)
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
