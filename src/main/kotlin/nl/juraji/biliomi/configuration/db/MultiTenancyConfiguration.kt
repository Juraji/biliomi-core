package nl.juraji.biliomi.configuration.db

import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "multi-tenancy")
data class MultiTenancyConfiguration(
    val tenants: List<Tenant> = emptyList(),
) {

    fun findTenant(tenantId: String): Tenant {
        return tenants.find { it.tenantId == tenantId }
            ?: throw IllegalArgumentException("Tenant with id $tenantId does not exist!")
    }
}

@ConstructorBinding
data class Tenant(
    val tenantId: String,
    val datasource: DataSourceProperties,
    val flyway: FlywayProperties?,
)
