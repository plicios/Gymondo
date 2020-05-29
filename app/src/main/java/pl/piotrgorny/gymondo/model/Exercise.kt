package pl.piotrgorny.gymondo.model

data class Exercise(
    val id: Long,
    val category: String,
    val name: String,
    val description: String,
    val mainImage: String,
    val images: List<String>,
    val equipment: List<String>,
    val muscles: List<String>
)