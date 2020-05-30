package pl.piotrgorny.gymondo.data.model

import androidx.recyclerview.widget.DiffUtil
import pl.piotrgorny.gymondo.data.dto.CategoryDto
import pl.piotrgorny.gymondo.data.dto.EquipmentDto
import pl.piotrgorny.gymondo.data.dto.ExerciseDto
import pl.piotrgorny.gymondo.data.dto.MuscleDto
import timber.log.Timber


data class Exercise(
    val id: Long,
    val category: String,
    val name: String,
    val description: String,
    val equipment: List<String>,
    val muscles: List<String>
) {
    constructor(exerciseDto: ExerciseDto, category: CategoryDto, equipment: List<EquipmentDto>, muscles: List<MuscleDto>) : this(
        exerciseDto.id,
        category.name,
        if(exerciseDto.name.isBlank()) exerciseDto.name_original else exerciseDto.name,
        exerciseDto.description,
        equipment.map { it.name },
        muscles.map { it.name }
    ){
        if(equipment.isNotEmpty()){
            Timber.e("$id, $name")
            equipment.forEach {
                Timber.e(it.name)
            }

        }
        if(muscles.isNotEmpty()){
            Timber.e("$id, $name")
            muscles.forEach {
                Timber.e(it.name)
            }
        }
    }

    companion object{
        val diffUtilCallback: DiffUtil.ItemCallback<Exercise> = object : DiffUtil.ItemCallback<Exercise>() {
            override fun areItemsTheSame(exercise1: Exercise, exercise2: Exercise): Boolean {
                return exercise1.id == exercise2.id
            }

            override fun areContentsTheSame(exercise1: Exercise, exercise2: Exercise): Boolean {
                return exercise1 == exercise2
            }
        }
    }
}