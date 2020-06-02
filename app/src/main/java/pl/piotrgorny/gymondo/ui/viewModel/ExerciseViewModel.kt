package pl.piotrgorny.gymondo.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.piotrgorny.gymondo.util.Event
import pl.piotrgorny.gymondo.util.ShowExerciseDetailsEvent
import pl.piotrgorny.gymondo.util.SingleLiveEvent
import pl.piotrgorny.gymondo.data.model.Exercise
import pl.piotrgorny.gymondo.data.repository.ExerciseInfoRepository

class ExerciseViewModel(val exercise: Exercise) : ViewModel() {
    val name = MutableLiveData(exercise.name)
    val category = MutableLiveData(exercise.category)
    val description = MutableLiveData(exercise.description)
    val equipment = MutableLiveData(exercise.equipment)
    val comaSeparatedEquipment = Transformations.map(equipment) { it.joinToString() }
    val muscles = MutableLiveData(exercise.muscles)
    val comaSeparatedMuscles = Transformations.map(muscles) { it.joinToString() }

    private val repository = ExerciseInfoRepository()

    val images = repository.getImages(exercise.id)
    val mainImage = Transformations.map(images) { it.firstOrNull { image -> image.is_main }?.image }

    var eventLiveData: SingleLiveEvent<Event>? = null

    fun openExerciseDetails() {
        eventLiveData?.postValue(
            ShowExerciseDetailsEvent(
                exercise
            )
        )
    }

    class Factory(
        private val exercise: Exercise
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExerciseViewModel(exercise) as T
        }
    }
}