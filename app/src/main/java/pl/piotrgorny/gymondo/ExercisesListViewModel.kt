package pl.piotrgorny.gymondo

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import pl.piotrgorny.gymondo.data.dto.CategoryDto
import pl.piotrgorny.gymondo.data.dto.EquipmentDto
import pl.piotrgorny.gymondo.data.dto.MuscleDto
import pl.piotrgorny.gymondo.data.model.Exercise
import pl.piotrgorny.gymondo.data.repository.ExercisesDataSource
import pl.piotrgorny.gymondo.data.repository.ExercisesFilterDataSource

class ExercisesListViewModel(categories: Map<Long, CategoryDto>, muscles: Map<Long, MuscleDto>, equipment: Map<Long, EquipmentDto>, eventLiveData: SingleLiveEvent<Event>) : ViewModel() {
    private val notFilteredExercises: LiveData<PagedList<Exercise>>

    val categoryFilter = MutableLiveData<CategoryDto?>(null)

    val exercises: LiveData<PagedList<Exercise>>

    init {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPrefetchDistance(4)
            .setPageSize(20)
            .build()
        notFilteredExercises = LivePagedListBuilder(ExercisesDataSource.Factory(categories, muscles, equipment, eventLiveData), config).build()

        exercises = Transformations.switchMap(categoryFilter) {
            if(it != null){
                LivePagedListBuilder(ExercisesFilterDataSource.Factory(muscles, equipment, it, eventLiveData),20).build()
            } else {
                notFilteredExercises
            }
        }
    }

    class Factory(
        private val categories: Map<Long, CategoryDto>,
        private val muscles: Map<Long, MuscleDto>,
        private val equipment: Map<Long, EquipmentDto>,
        private val eventLiveData: SingleLiveEvent<Event>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExercisesListViewModel(categories, muscles, equipment, eventLiveData) as T
        }
    }
}