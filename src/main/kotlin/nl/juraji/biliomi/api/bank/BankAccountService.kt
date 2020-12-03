package nl.juraji.biliomi.api.bank

import nl.juraji.biliomi.domain.bankaccount.commands.AddBankAccountBalanceCommand
import nl.juraji.biliomi.domain.bankaccount.commands.EndInterestCommand
import nl.juraji.biliomi.domain.bankaccount.commands.StartInterestCommand
import nl.juraji.biliomi.domain.bankaccount.commands.TakeBankAccountBalanceCommand
import nl.juraji.biliomi.projections.BankProjection
import nl.juraji.biliomi.projections.repositories.BankProjectionRepository
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class BankAccountService(
    private val bankProjectionRepository: BankProjectionRepository,
    private val reactorCommandGateway: ReactorCommandGateway
) {
    fun getAccountByUserId(userId: String): Mono<BankProjection> = bankProjectionRepository.findById(userId)
    fun getAllAccounts(): Flux<BankProjection> = bankProjectionRepository.findAll()
    fun addPoints(userId: String, amount: Long): Mono<Unit> =
        reactorCommandGateway.send(AddBankAccountBalanceCommand(userId, amount))

    fun takePoints(userId: String, amount: Long): Mono<Unit> =
        reactorCommandGateway.send(TakeBankAccountBalanceCommand(userId, amount))

    fun startInterest(userId: String): Mono<Unit> =
        reactorCommandGateway.send(StartInterestCommand(userId = userId))

    fun endInterest(userId: String): Mono<Unit> =
        reactorCommandGateway.send(EndInterestCommand(userId = userId))

}
