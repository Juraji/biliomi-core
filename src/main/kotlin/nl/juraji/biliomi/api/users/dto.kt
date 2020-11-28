package nl.juraji.biliomi.api.users

data class CreateUserDto(
        val username: String,
        val password: String,
        val roles: Set<String>,
)
