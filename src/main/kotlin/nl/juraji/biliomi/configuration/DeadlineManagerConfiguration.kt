package nl.juraji.biliomi.configuration

import org.axonframework.common.transaction.TransactionManager
import org.axonframework.config.ConfigurationScopeAwareProvider
import org.axonframework.deadline.DeadlineManager
import org.axonframework.deadline.quartz.QuartzDeadlineManager
import org.axonframework.spring.config.AxonConfiguration
import org.quartz.Scheduler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DeadlineManagerConfiguration {

    @Bean
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    fun deadlineManager(
            axonConfig: AxonConfiguration,
            transactionManager: TransactionManager,
            scheduler: Scheduler,
    ): DeadlineManager = QuartzDeadlineManager.builder()
            .transactionManager(transactionManager)
            .scopeAwareProvider(ConfigurationScopeAwareProvider(axonConfig))
            .scheduler(scheduler)
            .build()
}
