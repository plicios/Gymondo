package pl.piotrgorny.gymondo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import pl.piotrgorny.gymondo.data.model.Exercise
import pl.piotrgorny.gymondo.data.repository.Repository

class ExerciseViewModel(exercise: Exercise) : ViewModel() {
    val name = MutableLiveData(exercise.name)
    val category = MutableLiveData(exercise.category)
    val description = MutableLiveData(exercise.description)
    val equipment = MutableLiveData(exercise.equipment)
    val joinedEquipment = Transformations.map(equipment) { it.joinToString() }  //TODO rename
    val muscles = MutableLiveData(exercise.muscles)
    val joinedMuscles = Transformations.map(muscles) { it.joinToString() }  //TODO rename

    val repository = Repository()

    val images = repository.getImages(exercise.id)
    val mainImage = Transformations.map(images) { it.firstOrNull { image -> image.is_main }?.image }
}