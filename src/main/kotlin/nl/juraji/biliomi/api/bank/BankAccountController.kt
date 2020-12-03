package nl.juraji.biliomi.api.bank

import nl.juraji.biliomi.configuration.security.Authorities
import nl.juraji.biliomi.projections.BankProjection
import nl.juraji.biliomi.security.UserPrincipal
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/account")
class BankAccountController(
    private val bankAccountService: BankAccountService
) {

    @GetMapping("/me")
    @PreAuthorize("hasRole('${Authorities.BANK_READ_ME}')")
    fun getMyAccount(
        @AuthenticationPrincipal principal: UserPrincipal
    ): Mono<BankProjection> = bankAccountService.getAccountByUserId(principal.userId)

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('${Authorities.BANK_READ_ALL}')")
    fun getAccount(
        @PathVariable("userId") userId: String
    ): Mono<BankProjection> = bankAccountService.getAccountByUserId(userId)

    @GetMapping
    @PreAuthorize("hasRole('${Authorities.BANK_READ_ALL}')")
    fun getAllAccounts(): Flux<BankProjection> = bankAccountService.getAllAccounts()

    @PostMapping("/{userId}/points/add")
    @PreAuthorize("hasRole('${Authorities.BANK_ADD_POINTS}')")
    fun addPoints(
        @PathVariable("userId") userId: String,
        @RequestParam("amount") amount: Long
    ): Mono<Unit> = bankAccountService.addPoints(userId, amount)

    @PostMapping("/{userId}/points/take")
    @PreAuthorize("hasRole('${Authorities.BANK_TAKE_POINTS}')")
    fun takePoints(
        @PathVariable("userId") userId: String,
        @RequestParam("amount") amount: Long
    ): Mono<Unit> = bankAccountService.takePoints(userId, amount)

    @PostMapping("/{userId}/interest/start")
    @PreAuthorize("hasRole('${Authorities.BANK_START_STOP_INTEREST}')")
    fun startInterest(
        @PathVariable("userId") userId: String,
    ): Mono<Unit> = bankAccountService.startInterest(userId)

    @PostMapping("/{userId}/interest/end")
    @PreAuthorize("hasRole('${Authorities.BANK_START_STOP_INTEREST}')")
    fun endInterest(
        @PathVariable("userId") userId: String,
    ): Mono<Unit> = bankAccountService.endInterest(userId)
}
