package pl.piotrgorny.gymondo.data.dto

data class WgerApiResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)