package pl.piotrgorny.gymondo.data.repository

data class WgerApiResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)