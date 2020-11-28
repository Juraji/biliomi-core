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
import org.springframework.transaction.annotation.EnableTransactionManagement
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "securityEntityManagerFactory",
        transactionManagerRef = "securityTransactionManager",
        basePackages = ["nl.juraji.biliomi.security.repositories"]
)
class SecurityDataSourceConfiguration(
        multiTenancyConfiguration: MultiTenancyConfiguration,
) {
    private val tenant: Tenant = multiTenancyConfiguration.findTenant("security")

    @Bean(name = ["securityDataSource"])
    fun dataSource(): DataSource = tenant.datasource
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build()

    @Bean(name = ["securityEntityManagerFactory"])
    fun securityEntityManagerFactory(
            builder: EntityManagerFactoryBuilder,
            @Qualifier("securityDataSource") dataSource: DataSource,
    ): LocalContainerEntityManagerFactoryBean = builder
            .dataSource(dataSource)
            .packages(
                    "nl.juraji.biliomi.security"
            )
            .persistenceUnit("eventsourcing")
            .build()

    @Bean("securityTransactionManager")
    fun securityTransactionManager(
            @Qualifier("securityEntityManagerFactory") entityManagerFactory: EntityManagerFactory,
    ): PlatformTransactionManager = JpaTransactionManager(entityManagerFactory)

    @Bean(name = ["securityScheduler"])
    fun jdbcScheduler(
            @Qualifier("securityDataSource") dataSource: DataSource,
    ): Scheduler {
        val pool: ExecutorService = Executors.newFixedThreadPool(
                (dataSource as HikariDataSource).maximumPoolSize,
                NumberedThreadFactory("security-scheduler")
        )
        return Schedulers.fromExecutor(pool)
    }
}
