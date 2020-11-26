package nl.juraji.biliomi.configuration.db.projections

import com.zaxxer.hikari.HikariDataSource
import nl.juraji.biliomi.configuration.db.MultiTenancyConfiguration
import nl.juraji.biliomi.configuration.db.Tenant
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
class EventSourcingJpaConfiguration(
        multiTenancyConfiguration: MultiTenancyConfiguration,
) {
    private val tenant: Tenant = multiTenancyConfiguration.findTenant("axon")

    @Primary
    @Bean(name = ["dataSource"])
    fun dataSource(): DataSource = tenant.datasource
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build()

    @Primary
    @Bean(name = ["entityManagerFactory"])
    fun projectionsEntityManagerFactory(
            builder: EntityManagerFactoryBuilder,
            @Qualifier("dataSource") dataSource: DataSource,
    ): LocalContainerEntityManagerFactoryBean = builder
            .dataSource(dataSource)
            .packages(
                    "org.axonframework.eventhandling.tokenstore",
                    "org.axonframework.modelling.saga.repository.jpa",
                    "org.axonframework.eventsourcing.eventstore.jpa"
            )
            .persistenceUnit("eventsourcing")
            .build()

    @Primary
    @Bean("transactionManager")
    fun projectionsTransactionManager(
            @Qualifier("entityManagerFactory") entityManagerFactory: EntityManagerFactory,
    ): PlatformTransactionManager = JpaTransactionManager(entityManagerFactory)
}
