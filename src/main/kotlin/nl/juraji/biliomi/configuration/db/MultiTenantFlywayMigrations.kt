package nl.juraji.biliomi.configuration.db

import nl.juraji.biliomi.utils.LoggerCompanion
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor

@Configuration
class MultiTenantFlywayMigrations(
        @Qualifier("taskExecutor") private val taskExecutor: TaskExecutor?,
        private val multiTenancyConfiguration: MultiTenancyConfiguration,
) : InitializingBean {

    override fun afterPropertiesSet() {
        val tenants = multiTenancyConfiguration.tenants
        logger.info("Running Flyway migrations for ${tenants.size} tenants...")

        tenants
                .filter { it.flyway != null && it.flyway.isEnabled }
                .forEach { tenant ->
                    val flyway: Flyway = createFlywayMigrator(tenant.flyway!!, tenant.datasource)

                    if (taskExecutor != null) {
                        taskExecutor.execute {
                            logger.info("Running Flyway for [${tenant.tenantId}] asynchronously")
                            flyway.migrate()
                        }
                    } else {
                        logger.info("Running flyway for [${tenant.tenantId}] synchronously")
                        flyway.migrate()
                    }
                }
    }

    private fun createFlywayMigrator(
            flywayProperties: FlywayProperties,
            datasource: DataSourceProperties,
    ): Flyway {
        if (flywayProperties.url.isNullOrEmpty()) flywayProperties.url = datasource.url
        if (flywayProperties.user.isNullOrEmpty()) flywayProperties.user = datasource.username
        if (flywayProperties.password.isNullOrEmpty()) flywayProperties.password = datasource.password

        return Flyway.configure()
                .baselineOnMigrate(flywayProperties.isBaselineOnMigrate)
                .dataSource(flywayProperties.url, flywayProperties.user, flywayProperties.password)
                .locations(*flywayProperties.locations.toTypedArray())
                .load()
    }

    companion object : LoggerCompanion(MultiTenantFlywayMigrations::class)
}
