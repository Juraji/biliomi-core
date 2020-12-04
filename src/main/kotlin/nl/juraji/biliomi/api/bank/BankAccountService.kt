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
    fun getAccountByUsername(username: String): Mono<BankProjection> = bankProjectionRepository.findByUsername(username)
    fun getAllAccounts(): Flux<BankProjection> = bankProjectionRepository.findAll()
    fun addPoints(accountId: String, amount: Long, message: String?): Mono<Unit> =
        reactorCommandGateway.send(AddBankAccountBalanceCommand(accountId, amount, message))

    fun takePoints(accountId: String, amount: Long, message: String?): Mono<Unit> =
        reactorCommandGateway.send(TakeBankAccountBalanceCommand(accountId, amount, message))

    fun startInterest(accountId: String): Mono<Unit> =
        reactorCommandGateway.send(StartInterestCommand(accountId))

    fun endInterest(accountId: String): Mono<Unit> =
        reactorCommandGateway.send(EndInterestCommand(accountId))

}
