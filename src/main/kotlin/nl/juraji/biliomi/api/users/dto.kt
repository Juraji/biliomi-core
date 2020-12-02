package nl.juraji.biliomi.api.users

data class MutateUserDto(
    val username: String,
    val password: String,
)

data class CreateAuthorityGroupDto(
    val groupName: String,
    val authorities: Set<String>,
)
