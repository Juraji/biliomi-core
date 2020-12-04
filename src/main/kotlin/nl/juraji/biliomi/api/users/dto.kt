package nl.juraji.biliomi.api.users

data class CreateAuthorityGroupDto(
    val groupName: String,
    val authorities: Set<String>,
)

data class AuthorityGroupCreatedDTO(
    val groupId: String
)

data class CreateUserDto(
    val username: String, // Required and unique, identifies in authentication system
    val displayName: String?, // Optional, display name for user, defaults to username
    val password: String? // Optional, user will be disabled from API when null
)

data class UserCreatedDTO(
    val userId: String
)
