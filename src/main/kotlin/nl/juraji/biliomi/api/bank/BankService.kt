package nl.juraji.biliomi.api.bank

import nl.juraji.biliomi.domain.bank.commands.AddBalanceCommand
import nl.juraji.biliomi.domain.bank.commands.TakeBalanceCommand
import nl.juraji.biliomi.projections.BankProjection
import nl.juraji.biliomi.projections.repositories.BankProjectionRepository
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class BankService(
        private val bankProjectionRepository: BankProjectionRepository,
        private val reactorCommandGateway: ReactorCommandGateway
) {
    fun getAccountByUserId(userId: String): Mono<BankProjection> = bankProjectionRepository.findById(userId)
    fun getAllAccounts(): Flux<BankProjection> = bankProjectionRepository.findAll()
    fun addPoints(userId: String, amount: Long): Mono<Unit> = reactorCommandGateway.send(AddBalanceCommand(userId, amount))
    fun takePoints(userId: String, amount: Long): Mono<Unit> = reactorCommandGateway.send(TakeBalanceCommand(userId, amount))

}
