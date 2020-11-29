package nl.juraji.biliomi.utils.validation

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun <T : Any> Mono<T>.validate(block: Validator.(T) -> Unit): Mono<T> =
        this.map { next -> ValidatorImpl().apply { block.invoke(this, next) }.let { next } }

fun <T : Any> Mono<T>.validateAsync(block: AsyncValidator.(T) -> Unit): Mono<T> =
        this.flatMap { next -> AsyncValidatorImpl().apply { block.invoke(this, next) }.run().map { next } }

fun <T : Any> Flux<T>.validate(block: Validator.(T) -> Unit): Flux<T> =
        this.map { next -> ValidatorImpl().apply { block.invoke(this, next) }.let { next } }

fun <T : Any> Flux<T>.validateAsync(block: AsyncValidator.(T) -> Unit): Flux<T> =
        this.flatMap { next -> AsyncValidatorImpl().apply { block.invoke(this, next) }.run().map { next } }

