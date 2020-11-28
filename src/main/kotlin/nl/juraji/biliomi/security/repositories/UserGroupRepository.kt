package nl.juraji.biliomi.security.repositories

import nl.juraji.biliomi.security.AuthorityGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserGroupRepository : JpaRepository<AuthorityGroup, String> {
    fun findByName(groupName: String): Optional<AuthorityGroup>
}
