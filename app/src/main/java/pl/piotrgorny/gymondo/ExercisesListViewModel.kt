package pl.piotrgorny.gymondo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import pl.piotrgorny.gymondo.data.dto.CategoryDto
import pl.piotrgorny.gymondo.data.dto.EquipmentDto
import pl.piotrgorny.gymondo.data.dto.MuscleDto
import pl.piotrgorny.gymondo.data.model.Exercise
import pl.piotrgorny.gymondo.data.repository.ExercisesDataSource

class ExercisesListViewModel(categories: Map<Long, CategoryDto>, muscles: Map<Long, MuscleDto>, equipment: Map<Long, EquipmentDto>, eventLiveData: SingleLiveEvent<Event>) : ViewModel() {
    val exercises: LiveData<PagedList<Exercise>>

    init {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPrefetchDistance(4)
            .setPageSize(20)
            .build()
        exercises = LivePagedListBuilder(ExercisesDataSource.Factory(categories, muscles, equipment, eventLiveData), config).build()
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