package pl.piotrgorny.gymondo.data.dto

data class ExerciseDto(
    val id: Long,
    val license_author: String,
    val status: String,
    val description: String,
    val name: String,
    val name_original: String,
    val creation_date: String, //TODO check if can be replaced with Date
    val uuid: String,
    val license: Int,
    val category: Long,
    val language: Int,
    val muscles: List<Long>,
    val muscles_secondary: List<Long>,
    val equipment: List<Long>
) {
    fun matchNameFilter(nameFilter: String?) : Boolean {
        if(nameFilter == null)
            return true
        return name.contains(nameFilter, ignoreCase = true)
    }
}