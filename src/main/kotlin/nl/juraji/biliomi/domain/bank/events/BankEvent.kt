package nl.juraji.biliomi.domain.bank.events

import nl.juraji.biliomi.domain.DomainEvent

interface BankEvent : DomainEvent {
    val userId: String
}
