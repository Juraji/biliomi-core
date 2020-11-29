package nl.juraji.biliomi.configuration.axon

import org.axonframework.messaging.annotation.MetaDataValue

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MetaDataValue(value = UserPrincipalCommandInterceptor.USERNAME, required = true)
annotation class PrincipalUsername()
