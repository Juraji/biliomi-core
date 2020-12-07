package nl.juraji.biliomi.configuration.axon

import org.axonframework.common.transaction.TransactionManager
import org.axonframework.config.ConfigurationScopeAwareProvider
import org.axonframework.deadline.DeadlineManager
import org.axonframework.deadline.quartz.QuartzDeadlineManager
import org.axonframework.serialization.Serializer
import org.axonframework.spring.config.AxonConfiguration
import org.quartz.Scheduler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AxonQuartzSchedulerConfiguration {

    @Bean
    fun quartzDeadlineManager(
        scheduler: Scheduler,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") configuration: AxonConfiguration,
        transactionManager: TransactionManager,
        serializer: Serializer
    ): DeadlineManager = QuartzDeadlineManager.builder()
        .scheduler(scheduler)
        .scopeAwareProvider(ConfigurationScopeAwareProvider(configuration))
        .transactionManager(transactionManager)
        .serializer(serializer)
        .build()
}
