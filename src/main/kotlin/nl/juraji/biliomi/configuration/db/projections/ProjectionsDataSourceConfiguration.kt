package nl.juraji.biliomi.configuration.db.projections

import com.zaxxer.hikari.HikariDataSource
import nl.juraji.biliomi.configuration.db.MultiTenancyConfiguration
import nl.juraji.biliomi.configuration.db.Tenant
import nl.juraji.biliomi.utils.NumberedThreadFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource


@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "projectionsEntityManagerFactory",
        transactionManagerRef = "projectionsTransactionManager",
        basePackages = ["nl.juraji.biliomi.projections.repositories"]
)
class ProjectionsDataSourceConfiguration(
        multiTenancyConfiguration: MultiTenancyConfiguration,
) {
    private val tenant: Tenant = multiTenancyConfiguration.findTenant("projections")

    @Bean(name = ["projectionsDataSource"])
    fun dataSource(): DataSource = tenant.datasource
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build()

    @Bean(name = ["projectionsEntityManagerFactory"])
    fun projectionsEntityManagerFactory(
            builder: EntityManagerFactoryBuilder,
            @Qualifier("projectionsDataSource") dataSource: DataSource,
    ): LocalContainerEntityManagerFactoryBean = builder
            .dataSource(dataSource)
            .packages(
                    "nl.juraji.biliomi.projections"
            )
            .persistenceUnit(tenant.tenantId)
            .build()

    @Bean("projectionsTransactionManager")
    fun projectionsTransactionManager(
            @Qualifier("projectionsEntityManagerFactory") entityManagerFactory: EntityManagerFactory,
    ): PlatformTransactionManager = JpaTransactionManager(entityManagerFactory)

    @Bean(name = ["projectionsScheduler"])
    fun jdbcScheduler(
            @Qualifier("projectionsDataSource") dataSource: DataSource,
    ): Scheduler {
        val pool: ExecutorService = Executors.newFixedThreadPool(
                (dataSource as HikariDataSource).maximumPoolSize,
                NumberedThreadFactory("projections-scheduler")
        )
        return Schedulers.fromExecutor(pool)
    }
}
