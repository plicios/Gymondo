package pl.piotrgorny.gymondo.data.dto

data class ImageDto(
    val id: Long,
    val license_author: String,
    val status: String,
    val image: String,
    val is_main: Boolean,
    val license: Int,
    val exercise: Long
)