package nl.juraji.biliomi.domain.bankaccount.events

import nl.juraji.biliomi.domain.DomainEvent

interface BankAccountEvent : DomainEvent {
    val userId: String
}
