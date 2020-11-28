package nl.juraji.biliomi.security.repositories

import nl.juraji.biliomi.security.UserGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserGroupRepository : JpaRepository<UserGroup, String> {
    fun findByName(groupName: String): Optional<UserGroup>
}
