package nl.juraji.biliomi.configuration.axon

import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Configuration

@Configuration
class ReactorGatewayConfiguration(
        private val commandGateway: ReactorCommandGateway
) : InitializingBean {

    override fun afterPropertiesSet() {
        commandGateway.registerDispatchInterceptor(UserPrincipalCommandInterceptor())
    }
}
