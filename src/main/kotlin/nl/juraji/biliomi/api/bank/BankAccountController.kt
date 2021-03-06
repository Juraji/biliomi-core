package nl.juraji.biliomi.api.bank

import nl.juraji.biliomi.configuration.security.Authorities
import nl.juraji.biliomi.projections.BankProjection
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/bank")
class BankAccountController(
    private val bankAccountService: BankAccountService
) {

    @GetMapping("/me")
    @PreAuthorize("hasRole('${Authorities.BANK_READ_ME}')")
    fun getMyAccount(
        @AuthenticationPrincipal principal: UserDetails
    ): Mono<BankProjection> = bankAccountService.getAccountByUsername(principal.username)

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('${Authorities.BANK_READ_ALL}')")
    fun getAccount(
        @PathVariable("username") username: String
    ): Mono<BankProjection> = bankAccountService.getAccountByUsername(username)

    @GetMapping
    @PreAuthorize("hasRole('${Authorities.BANK_READ_ALL}')")
    fun getAllAccounts(): Flux<BankProjection> = bankAccountService.getAllAccounts()

    @PostMapping("/{accountId}/points/mutate")
    @PreAuthorize("hasRole('${Authorities.BANK_MUTATE_POINTS}')")
    fun addPoints(
        @PathVariable("accountId") accountId: String,
        @RequestParam("amount") amount: Long,
        @RequestParam("message", required = false) message: String?,
    ): Mono<Unit> = bankAccountService.mutatePoints(accountId, amount, message)

    @PostMapping("/{accountId}/interest/start")
    @PreAuthorize("hasRole('${Authorities.BANK_START_STOP_INTEREST}')")
    fun startInterest(
        @PathVariable("accountId") accountId: String,
    ): Mono<Unit> = bankAccountService.startInterest(accountId)

    @PostMapping("/{accountId}/interest/end")
    @PreAuthorize("hasRole('${Authorities.BANK_START_STOP_INTEREST}')")
    fun endInterest(
        @PathVariable("accountId") accountId: String,
    ): Mono<Unit> = bankAccountService.endInterest(accountId)
}
