package nl.juraji.biliomi.configuration.axon

import org.axonframework.config.ConfigurationScopeAwareProvider
import org.axonframework.deadline.DeadlineManager
import org.axonframework.deadline.quartz.QuartzDeadlineManager
import org.axonframework.serialization.Serializer
import org.axonframework.spring.config.AxonConfiguration
import org.axonframework.spring.eventhandling.scheduling.quartz.QuartzEventSchedulerFactoryBean
import org.quartz.Scheduler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager


@Configuration
class AxonQuartzSchedulerConfiguration {

    @Bean
    fun quartzEventSchedulerFactoryBean(): QuartzEventSchedulerFactoryBean = QuartzEventSchedulerFactoryBean()

    @Bean
    fun quartzDeadlineManager(
        scheduler: Scheduler,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") configuration: AxonConfiguration,
        @Qualifier("transactionManager") transactionManager: PlatformTransactionManager,
        serializer: Serializer
    ): DeadlineManager = QuartzDeadlineManager.builder()
        .scheduler(scheduler)
        .scopeAwareProvider(ConfigurationScopeAwareProvider(configuration))
//        .transactionManager(transactionManager)
        .serializer(serializer)
        .build()
}
