package nl.juraji.biliomi.api.users

data class PointBalanceDTO(
        val userId: String,
        val pointBalance: Long,
)

data class CreateUserDTO(
        val userId: String?,
        val username: String,
)
