package nl.juraji.biliomi.configuration.aggregate

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@ConfigurationProperties(prefix = "bank-account")
@ConstructorBinding
data class BankAccountAggregateConfiguration(
    val interestRateMinutes: Long = 5,
    val interestAmount: Long = 10
) {
    val interestRateDuration: Duration
        get() = Duration.ofMinutes(interestRateMinutes)
}
