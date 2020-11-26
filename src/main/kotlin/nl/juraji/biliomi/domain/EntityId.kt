package nl.juraji.biliomi.domain

import java.io.Serializable

interface EntityId : Serializable {
    val identifier: String
}
