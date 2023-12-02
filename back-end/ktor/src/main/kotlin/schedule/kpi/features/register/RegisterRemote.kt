package schedule.kpi.features.register

import kotlinx.serialization.Serializable


@Serializable
data class RegisterReceiveRemote(
    val login: String,
    val email: String,
    val password: String,
)

@Serializable
data class RegisterResponseModel(
    val token: String,
    val message: String

)